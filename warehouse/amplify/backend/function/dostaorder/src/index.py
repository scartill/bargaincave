import os
import traceback
import json

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidAPI
from bargain_cave_bot import service_message
from dostavista import DostavistaWebhook, DostavistaError

ECWID_SECRET_NAME = 'ecwid_api'
DOSTA_SECRET_NAME = 'dostavista_api'


def process_dosta_event(event, wh, api):
    dosta_event = wh.process(event)
    print('DOSTA EVENT', dosta_event)


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
