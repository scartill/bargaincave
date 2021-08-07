import requests
import hmac
import hashlib
import json

from rest import norm_headers


DOST_ON_FOOT = 6
DOST_BY_CAR = 7

class DostavistaError(RuntimeError):
    pass


class DostavistaAPI:
    
    def __init__(self, domain, api_key):
        api_base = 'api/business/1.1'

        self.base_url = f'https://{domain}/{api_base}'

        self.headers = {
            'X-DV-Auth-Token': api_key
        }

        self.bankcard_id = 'none'

    def set_origin(self, dispatch_address, dispatch_phone):
        self.dispatch_address = dispatch_address
        self.dispatch_phone = dispatch_phone

    def set_payment_method(self, method, bankcard_id=None):
        if not method in ['cash', 'non_cash', 'bank_card']:
            raise DostavistaError(f'Bad payment method {method}')

        if method == 'bank_card' and not bankcard_id:
            raise DostavistaError('Bank card ID required')

        self.payment_method = method
        self.bankcard_id = bankcard_id

    def post(self, endpoint, payload):
        r = requests.post(f'{self.base_url}/{endpoint}', headers=self.headers, json=payload).json()
        
        if not r['is_successful']:
            dosta_error = f"{r['errors']}, {r.get('parameter_errors')}"
            print(f"Dostavista error :: {dosta_error}")
            raise DostavistaError(f'Dostavista API error {dosta_error}')

        return r['order']

    def _fill_basic_props(self, order_props):
        return {
            'type': 'standard',
            'matter': 'Продукты',
            'is_client_notification_enabled': True,
            'total_weight_kg': order_props['weight_kg'],
            'points': [
                {
                    'address': self.dispatch_address
                },
                {
                    'address': order_props['target']
                }
            ]
        }

    def get_price(self, order_props):
        order = self._fill_basic_props(order_props)
        return self.post('calculate-order', order)

    def place_order(self, order_props):
        order = self._fill_basic_props(order_props)

        order['points'][0]['client_order_id'] = order_props['internal_order_id']
        order['points'][0]['contact_person'] = {
            'phone': self.dispatch_phone
        }

        order['points'][1]['note'] = order_props['customer_note']
        order['points'][1]['contact_person'] = {
            'name': order_props['customer_name'],
            'phone': order_props['customer_phone']
        }

        order['payment_method'] = self.payment_method

        if self.payment_method == 'bank_card':
            order['bank_card_id'] = self.bankcard_id
        else:
            print(f'Dostavista :: Using payment method {self.payment_method}')

        return self.post('create-order', order)


class DostavistaWebhook:

    def __init__(self, callback_token):
        self.secret = callback_token

    def verify_signature(self, signed_data, signature):
        signature_computed = hmac.new(
            key=self.secret.encode(),
            msg=signed_data.encode(),
            digestmod=hashlib.sha256
        ).hexdigest()

        if signature != signature_computed:
            raise RuntimeError('Invalid Dosta signature')

    def process(self, payload):
        headers = norm_headers(payload['headers'])

        signature = headers['x-dv-signature']
        self.verify_signature(payload['body'], signature)
        print('Dostavista signature OK')

        event = json.loads(payload['body'])

        return event

