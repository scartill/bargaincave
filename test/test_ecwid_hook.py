import os
import json
from pathlib import Path
import hashlib
import hmac
import base64

class EcwidWebhook:
    
    def __init__(self, client_secret):
        self.secret = client_secret

    def verify_signature(self, event_id, event_created, signature):
        signed_data = f'{event_created}.{event_id}'

        signature_computed_bytes = hmac.new(
            key=self.secret.encode(),
            msg=signed_data.encode(),
            digestmod=hashlib.sha256
        ).digest()

        signature_computed = base64.b64encode(signature_computed_bytes).decode()

        if signature != signature_computed:
            raise RuntimeError('Invalid Ecwid signature')

    def process(self, payload):
        version = payload['version']
        if version != '2.0':
            raise RuntimeError(f'Bad incoming Ecwid hook version {version}')

        body = json.loads(payload['body'])
        event_id = body['eventId']
        event_created = body['eventCreated']
        signature = payload['headers']['x-ecwid-webhook-signature']
        self.verify_signature(event_id, event_created, signature)

        return {
            'EventType': payload['queryStringParameters']['eventtype'],
            'EventData': body['data']
        }


client_secret = os.getenv('ECWID_TEST_CLIENT')
ew = EcwidWebhook(client_secret)


def test_new_order():
    payload = json.loads(Path('data/ecwid_new_order_hook.json').read_text())
    print(ew.process(payload))


def test_updated_order():
    payload = json.loads(Path('data/ecwid_updated_order_hook.json').read_text())
    print(ew.process(payload))

test_new_order()
test_updated_order()
