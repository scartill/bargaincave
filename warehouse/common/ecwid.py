import requests
import json
import hashlib
import hmac
import base64


class EcwidAPI:

    def __init__(self, domain, store_id, public_token, secret_token):
        api_base = 'api/v3'

        self.base_url = f'https://{domain}/{api_base}/{store_id}'

        self.headers = {
            'Accept': 'application/json'
        }

        self.public = public_token
        self.secret = secret_token

    def _get_with_key(self, endpoint, key):
        url = f'{self.base_url}/{endpoint}?token={key}'
        r = requests.get(url, headers=self.headers)
        r.raise_for_status()
        return r.json()

    def get(self, endpoint):
        return self._get_with_key(endpoint, self.public)

    def get_private(self, endpoint):
        return self._get_with_key(endpoint, self.secret)

    def post(self, endpoint, payload):
        url = f'{self.base_url}/{endpoint}?token={self.secret}'
        r = requests.post(url, headers=self.headers, json=payload)
        r.raise_for_status()
        return r.json()

    def categories(self):
        return self.get('categories')

    def products(self):
        return self.get('products')

    def create_product(self, product):
        return self.post('products', product)

    def order(self, order_id):
        return self.get_private(f'orders/{order_id}')

    def update_order(self, order_id, order_data):
        return self.post(f'orders/{order_id}', order_data)


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
        body = json.loads(payload['body'])
        event_id = body['eventId']
        event_created = body['eventCreated']
        signature = payload['headers']['X-Ecwid-Webhook-Sgnature']
        self.verify_signature(event_id, event_created, signature)

        return {
            'EventType': payload['queryStringParameters']['eventtype'],
            'EventData': body['data']
        }

   