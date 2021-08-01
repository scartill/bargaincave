import os
import traceback
import json

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidWebhook, EcwidAPI
from bargain_cave_bot import service_message
from dostavista import DostavistaAPI, DostavistaError

ECWID_SECRET_NAME = 'ecwid_api'
DOSTA_SECRET_NAME = 'dostavista_api'


def process_paid_order(ecwid_order, dosta):
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

        target = '{}, {}'.format(
            customer['city'],
            customer['street']
        )
        note = ecwid_order['orderComments']

        weight = sum(item['weight'] for item in ecwid_order['items'])

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
        print('DOSTA ORDER', dosta_order)

        dosta_id = dosta_order['order_id']
        service_message(
            f'Delivery called for {ecwid_id} with {dosta_id}'
        )

        return (True, dosta_order)
    except DostavistaError as de:
        service_message(f'Dostavista delivery call failed ({de})')
        return (False, None)


def update_ecwid_order(api, ecwid_order, dosta_order):
    ecwid_id = ecwid_order['id']
    dosta_id = dosta_order['order_id']
    ecwid_order['externalFulfillment'] = True
    ecwid_order['externalOrderId'] = str(dosta_id)

    api.update_order(ecwid_id, ecwid_order)


def process_ecwid_event(event, wh, api, dosta):
    ecwid_event = wh.process(event)
    print('ECWID EVENT', ecwid_event)

    if ecwid_event['EventType'] != 'order.updated':
        # This is the only one we need now
        print('Order in wrong status :: ignoring')
        return

    old_status = ecwid_event['EventData']['oldPaymentStatus']
    new_status = ecwid_event['EventData']['newPaymentStatus']

    was_paid = (old_status != 'PAID') and (new_status == 'PAID')

    if not was_paid:
        # This is the only situation we need
        return

    order_id = ecwid_event['EventData']['orderId']
    ecwid_order = api.order(order_id)
    print('ECWID ORDER', ecwid_order)

    proceed, dosta_order = process_paid_order(ecwid_order, dosta)
    if not proceed:
        print('Delivery call failed :: exiting')
        return

    print('Order update :: setting delivery')
    try:
        # This should not fail in order to avoid double delivery
        update_ecwid_order(api, ecwid_order, dosta_order)
    except Exception as e:
        service_message(f'Ecwid order update failed :: see logs')
        traceback.print_exc()


def accept_webhook(event):
    ecwid_tokens = json.loads(
        get_sm_secret(f"{ECWID_SECRET_NAME}-{os.getenv('ENV')}")
    )

    wh = EcwidWebhook(ecwid_tokens['Client'])
    api = EcwidAPI(
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
    dosta.set_bankcard(
        dosta_tokens['BankCardID']
    )
        
    process_ecwid_event(event, wh, api, dosta)


def handler(event, context):
    print('EVENT', event)
    try:
        accept_webhook(event)
        return make_response()
    except Exception as e:
        service_message(f'Ecwid webhook failed :: see logs')
        traceback.print_exc()
        # NB: responding with 200 due to Ecwid Webhook handling policy
        return make_response()
