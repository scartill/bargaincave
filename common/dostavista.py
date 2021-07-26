import os
import requests
import json
from argparse import ArgumentParser
import logging

DOST_ON_FOOT = 6
DOST_BY_CAR = 7


class DostavistaAPI:
    
    def __init__(self, domain, api_key):
        api_base = 'api/business/1.1'

        self.base_url = f'https://{domain}/{api_base}'

        self.headers = {
            'X-DV-Auth-Token': api_key
        }

    def post(self, endpoint, payload):
        r = requests.post(f'{self.base_url}/{endpoint}', headers=self.headers, json=payload).json()
        
        if not r['is_successful']:
            logging.error(f'DOST ERROR: {r['errors']}, {r.get('parameter_errors')}'')
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

dost = DostavistaAPI(os.getenv('DOST_DOMAIN'), os.getenv('DOST_KEY'))


def cli_getargs():
    parser = ArgumentParser()
    parser.add_argument('-v', "--verbose", action='store_true', help="sets logging level to debug")
    parser.add_argument('-p', "--price", action='store_true', help="request test price")
    parser.add_argument('-o', "--order", action='store_true', help="place test order")
    return parser.parse_args()

def main():
    args = cli_getargs()
    logging.basicConfig(level = logging.DEBUG if args.verbose else logging.INFO)

    if args.price:
        print(dost.get_price(order_props)['delivery_fee_amount'])

    if args.order:
        dump(dost.place_order(order_props))

if __name__ == '__main__':
    main()
