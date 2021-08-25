import os
import traceback
import json

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidAPI
from bargain_cave_bot import service_message
from dostavista import DostavistaWebhook

ECWID_SECRET_NAME = 'ecwid_api'
DOSTA_SECRET_NAME = 'dostavista_api'


def set_fulfillment(order, fulfillment):
    order['fulfillmentStatus'] = fulfillment


def process_dosta_event(event, wh, api):
    dosta_event = wh.process(event)
    event_type = dosta_event['event_type']

    update_ecwid = None

    if event_type == 'order_changed':
        dosta_order_id = dosta_event['order']['order_id']
        order_status = dosta_event['order']['status']
        ecwid_order_id = dosta_event['order']['points'][0]['client_order_id']

        if order_status == 'available':
            service_message(f'Delivery {dosta_order_id} approved for {ecwid_order_id}')

        if order_status == 'active':
            service_message(f'Delivery {dosta_order_id} active for {ecwid_order_id}')
            update_ecwid = lambda eo: set_fulfillment(eo, 'PROCESSING')

        if order_status == 'completed':
            service_message(f'Delivery {dosta_order_id} completed for {ecwid_order_id}')
            update_ecwid = lambda eo: set_fulfillment(eo, 'DELIVERED')

        if order_status == 'canceled':
            service_message(f'Delivery {dosta_order_id} canceled for {ecwid_order_id}')
            update_ecwid = lambda eo: set_fulfillment(eo, 'WILL_NOT_DELIVER')

        if order_status == 'delayed':
            service_message(f'Delivery {dosta_order_id} delayed for {ecwid_order_id}')

    if update_ecwid:
        ecwid_order = api.order(ecwid_order_id)
        update_ecwid(ecwid_order)
        api.update_order(ecwid_order_id, ecwid_order)
        print('Dostavista callback :: order updated')


def accept_webhook(event):
    ecwid_tokens = json.loads(
        get_sm_secret(f"{ECWID_SECRET_NAME}-{os.getenv('ENV')}")
    )

    api = EcwidAPI(
        os.getenv('ECWID_DOMAIN'),
        ecwid_tokens['StoreID'],
        ecwid_tokens['Public'],
        ecwid_tokens['Secret']
    )

    dosta_tokens = json.loads(
        get_sm_secret(f"{DOSTA_SECRET_NAME}-{os.getenv('ENV')}")
    )

    wh = DostavistaWebhook(
        dosta_tokens['CallbackKey']
    )

    process_dosta_event(event, wh, api)


def handler(event, context):
    print('EVENT', event)
    try:
        accept_webhook(event)
        return make_response()
    except Exception as e:
        service_message(f'Dostavista webhook failed :: see logs')
        traceback.print_exc()
        # NB: responding with 200 due to Dostavista Webhook handling policy
        return make_response()
