import os
import requests
import json

DOST_ON_FOOT = 6
DOST_BY_CAR = 7


class DostavistaAPI:
    
    def __init__(self):
        api_base = 'api/business/1.1'

        domain = os.getenv('DOST_DOMAIN')
        print('DOST DOMAIN', domain)

        api_key = os.getenv('DOST_KEY')
        print('DOST KEY', api_key[0:3])

        self.base_url = f'https://{domain}/{api_base}'

        self.headers = {
            'X-DV-Auth-Token': api_key
        }

    def post(self, endpoint, payload):
        r = requests.post(f'{self.base_url}/{endpoint}', headers=self.headers, json=payload).json()
        
        if not r['is_successful']:
            print('DOST ERROR', r['errors'], r.get('parameter_errors'))
            raise RuntimeError('Dostavista API call error')

        return r['order']

    def _fill_basic_props(self, order_props):
        return {
            'type': 'standard',
            'matter': 'Продукты',
            'is_client_notification_enabled': True,
            'total_weight_kg': order_props['weight_kg'],
            'points': [
                {
                    'address': order_props['origin']
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

        order['points'][0]['contact_person'] = {
            'phone': order_props['dispatcher_phone']
        }

        order['points'][1]['contact_person'] = {
            'phone': order_props['customer_phone']
        }

        print(order)

        return self.post('create-order', order)
    

def dump(r):
    print(json.dumps(r, indent=2))

order_props = {
    'origin': 'Москва, Михайловский проезд, 5',
    'target': 'Москва, улица Острякова, 5',
    'weight_kg': 4.0,
    'dispatcher_phone': '+79161437959',
    'customer_phone': '+79161437959'
}

dost = DostavistaAPI()

print(dost.get_price(order_props)['delivery_fee_amount'])
dump(dost.place_order(order_props))
