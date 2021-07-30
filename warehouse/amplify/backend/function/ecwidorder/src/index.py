import os
import traceback
import json

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidWebhook, EcwidAPI

ECWID_SECRET_NAME = 'ecwid_api'


def process_ecwid_event(ecwid_event):
    print('ECWID EVENT', ecwid_event)

    if ecwid_event['EventType'] != 'order.updated':
        # This is the only one we need now
        return

    old_status = ecwid_event['EventData']['oldPaymentStatus']
    new_status = ecwid_event['EventData']['newPaymentStatus']

    was_paid = (old_status != 'PAID') and (new_status == 'PAID')

    if was_paid:
        print('Order paid :: processing')

    order_id = ecwid_event['EventData']['orderId']



def accept_webhook(event):
    ecwid_tokens = json.loads(
        get_sm_secret(f"{ECWID_SECRET_NAME}-{os.getenv('ENV')}")
    )

    wh = EcwidWebhook(ecwid_tokens['Client'])
    ec_event = wh.process(event)
    process_ecwid_event(ec_event)


def handler(event, context):
    print('EVENT', event)
    try:
        accept_webhook(event)
        return make_response()
    except Exception as e:
        traceback.print_exc()
        return make_response(error=e)