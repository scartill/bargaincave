import requests
import json
import os


class EcwidAPI:

    def __init__(self, domain, store_id, public_token, secret_token):
        api_base = 'api/v3'

        self.base_url = f'https://{domain}/{api_base}/{store_id}'

        self.headers = {
            'Accept': 'application/json'
        }

        self.public = public_token
        self.secret = secret_token

    def get(self, endpoint):
        url = f'{self.base_url}/{endpoint}?token={self.public}'
        print(url)
        r = requests.get(url, headers=self.headers)
        r.raise_for_status()
        return r.json()

    def post(self, endpoint, payload):
        url = f'{self.base_url}/{endpoint}?token={self.secret}'
        print(url)
        r = requests.post(url, headers=self.headers, json=payload)
        r.raise_for_status()
        return r.json()

    def categories(self):
        return self.get('categories')

    def products(self):
        return self.get('products')

    def create_test_product(self):
        product = {
            "sku": "000012199",
            "quantity": 10,
            "name": "New Product",
            "nameTranslated": {
                "en": "New Product",
                "es": "Nuevo producto"
            },
            "price": 20.99,
            "isShippingRequired": False,
            "categoryIds": [
                114263001
            ],
            "weight": 10,
            "enabled": True,
            "description": "A <b>new</b> product description",
        }
        return self.post('products', product)
    

ecwid = EcwidAPI(
    os.getenv('ECWID_DOMAIN'),
    os.getenv('ECWID_STORE'),
    os.getenv('ECWID_PUBLIC'),
    os.getenv('ECWID_SECRET')
)

cs = ecwid.categories()['items']
[print(c['id'], c['name']) for c in cs]

print(json.dumps(ecwid.create_test_product(), indent=2))
