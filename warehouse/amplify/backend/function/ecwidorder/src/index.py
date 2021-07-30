import os
import traceback
import json
from warehouse.amplify.backend.function.ecwidorder.src.dostavista import DostavistaError

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidWebhook, EcwidAPI
from bargain_cave_bot import service_message

ECWID_SECRET_NAME = 'ecwid_api'


def process_paid_order(order):
    print('Order paid :: processing')
    shipping = order['shippingOption']

    if shipping['isPickup']:
        print('Self-pick order :: ignoring')
        return

    try:
        order_props = {
            'origin': 'Москва, Михайловский проезд, 5',
            'target': 'Москва, улица Острякова, 5',
            'weight_kg': 4.0,
            'dispatcher_phone': '+79161437959',
            'customer_phone': '+79161437959'
        }
    except DostavistaError as de:
        service_message(f'Dostavista delivery call failed ({de})')


def process_ecwid_event(event, wh, api):
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

    process_paid_order(order)


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

#    dosta = DostavistaAPI(
#        os.getenv('DOSTA_DOMAIN'), 
#        dosta_tokens('APIKey')
#    )
        
    process_ecwid_event(event, wh, api)


def handler(event, context):
    print('EVENT', event)
    try:
        accept_webhook(event)
        return make_response()
    except Exception as e:
        traceback.print_exc()
        return make_response(error=e)