import os
import traceback
import json

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidWebhook, EcwidAPI
from bargain_cave_bot import service_message
from dostavista import DostavistaAPI, DostavistaError
from dadata import DadataAPI

ECWID_SECRET_NAME = 'ecwid_api'
DOSTA_SECRET_NAME = 'dostavista_api'
DADATA_SECRET_NAME = 'dadata_api'


def refine_address(dadata, ecwid_id, city, street):
    response = dadata.clean_address(f'{city}, {street}')

    if response['is_check_required']:
        service_message(f'Custom address check required for {ecwid_id}')

    address = response['address']

    if not address:
        print(f'Delivery address empty for {ecwid_id}')
        address = ''

    freetext = response['remainder']

    if not freetext:
        freetext = ''

    return address, freetext


def process_paid_order(ecwid_order, dosta, dadata):
    print('Order paid :: processing')
    ecwid_id = ecwid_order['id']
    shipping = ecwid_order['shippingOption']

    if shipping['isPickup']:
        print('Self-pick order :: ignoring')
        return (False, None)

    try:
        customer = ecwid_order['shippingPerson']

        name = customer.get('name')

        if not name:
            service_message(f'No customer name for {ecwid_id}')

        phone = customer.get('phone')

        if not phone:
            service_message(f'No customer phone for {ecwid_id}')
            return (False, None)

        target, freetext = refine_address(
            dadata, ecwid_id,
            customer['city'], customer['street']
        )

        note = ' '.join([freetext, ecwid_order['orderComments']]).strip()
        weight = sum(item['weight']*item['quantity'] for item in ecwid_order['items'])

        if weight == 0.0:
            service_message(f'Order weight is zero ({ecwid_id})')
            return (False, None)

        order_props = {
            'internal_order_id': ecwid_id,
            'target': target,
            'weight_kg': weight,
            'customer_phone': phone,
            'customer_name': name,
            'customer_note': note
        }

        dosta_order = dosta.place_order(order_props)
        dosta_id = dosta_order['order_id']
        print(f'Delivery called for {ecwid_id} with {dosta_id}')

        return (True, dosta_order)
    except DostavistaError as de:
        service_message(f'Dostavista delivery call failed ({de})')
        return (False, None)


def update_ecwid_order(ecwid, ecwid_order, dosta_order):
    ecwid_id = ecwid_order['id']
    dosta_id = dosta_order['order_id']
    ecwid_order['externalFulfillment'] = True
    ecwid_order['externalOrderId'] = str(dosta_id)

    ecwid.update_order(ecwid_id, ecwid_order)


def process_ecwid_event(event, wh, ecwid, dosta, dadata):
    ecwid_event = wh.process(event)

    was_paid = False

    if ecwid_event['EventType'] == 'order.created':
        status = ecwid_event['EventData']['newPaymentStatus']
        was_paid = status == 'PAID'

    if ecwid_event['EventType'] == 'order.updated':
        old_status = ecwid_event['EventData']['oldPaymentStatus']
        new_status = ecwid_event['EventData']['newPaymentStatus']
        was_paid = was_paid or (old_status != 'PAID') and (new_status == 'PAID')

    if not was_paid:
        print('Order transition not important :: exiting')
        # This is the only situation we need
        return

    order_id = ecwid_event['EventData']['orderId']
    ecwid_order = ecwid.order(order_id)

    proceed, dosta_order = process_paid_order(ecwid_order, dosta, dadata)
    if not proceed:
        print('Delivery failed or not requisted :: exiting')
        return

    print('Order update :: setting delivery')
    try:
        # This should not fail in order to avoid double delivery
        update_ecwid_order(ecwid, ecwid_order, dosta_order)
    except Exception as e:
        service_message(f'Ecwid order update failed :: see logs')
        traceback.print_exc()


def accept_webhook(event):
    ecwid_tokens = json.loads(
        get_sm_secret(f"{ECWID_SECRET_NAME}-{os.getenv('ENV')}")
    )

    wh = EcwidWebhook(ecwid_tokens['Client'])

    ecwid = EcwidAPI(
        os.getenv('ECWID_DOMAIN'),
        ecwid_tokens['StoreID'],
        ecwid_tokens['Public'],
        ecwid_tokens['Secret']
    )

    dosta_tokens = json.loads(
        get_sm_secret(f"{DOSTA_SECRET_NAME}-{os.getenv('ENV')}")
    )

    dosta = DostavistaAPI(
        os.getenv('DOSTA_DOMAIN'),
        dosta_tokens['APIKey']
    )

    dosta.set_origin(
        dosta_tokens['DispatchAddress'],
        dosta_tokens['DispatchPhone']
    )

    dosta.set_payment_method(
        dosta_tokens['PaymentMethod'],
        dosta_tokens['BankCardID']
    )

    dadata_tokens = json.loads(
        get_sm_secret(f"{DADATA_SECRET_NAME}-{os.getenv('ENV')}")
    )

    dadata = DadataAPI(
        os.getenv('DADATA_DOMAIN'),
        dadata_tokens['Token'],
        dadata_tokens['Secret']
    )

    process_ecwid_event(event, wh, ecwid, dosta, dadata)


def handler(event, context):
    try:
        accept_webhook(event)
        return make_response()
    except Exception as e:
        print('OFFENDING EVENT', event)
        service_message(f'Ecwid webhook failed :: see logs')
        traceback.print_exc()
        # NB: responding with 200 due to Ecwid Webhook handling policy
        return make_response()
