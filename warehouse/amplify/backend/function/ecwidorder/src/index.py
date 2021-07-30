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
        return

    try:
        order_props = {
            'internal_order_id': ecwid_id,
            'target': 'Москва, улица Острякова, 5',
            'weight_kg': 4.0,
            'customer_phone': '+79161437959',
            'customer_name': 'Boris',
            'customer_note': 'Позвонить за полчаса'
        }

        dosta_order = dosta.place_order(order_props)
        
        print('DOSTA ORDER', dosta_order)

        dosta_id = dosta_order['order_id']

        service_message(
            f'Delivery called for {ecwid_id} with {dosta_id}'
        )
    except DostavistaError as de:
        service_message(f'Dostavista delivery call failed ({de})')


def process_ecwid_event(event, wh, api, dosta):
    ecwid_event = wh.process(event)
    print('ECWID EVENT', ecwid_event)

    if ecwid_event['EventType'] != 'order.updated':
        # This is the only one we need now
        return

    old_status = ecwid_event['EventData']['oldPaymentStatus']
    new_status = ecwid_event['EventData']['newPaymentStatus']

    was_paid = (old_status != 'PAID') and (new_status == 'PAID')

    if not was_paid:
        # This is the only situation we need
        return

    order_id = ecwid_event['EventData']['orderId']
    order = api.order(order_id)
    print('ECWID ORDER', order)

    process_paid_order(order, dosta)


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
        dosta_tokens('APIKey')
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
        traceback.print_exc()
        return make_response(error=e)