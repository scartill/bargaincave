import os
import json

from ecwid import EcwidAPI

ecwid = EcwidAPI(
    os.getenv('ECWID_DOMAIN'),
    os.getenv('ECWID_STORE'),
    os.getenv('ECWID_PUBLIC'),
    os.getenv('ECWID_SECRET')
)

product = {
    "sku": "000012191",
    "quantity": 10,
    "name": "New Product 1",
    "nameTranslated": {
        "en": "New Product",
        "es": "Nuevo producto"
    },
    "price": 20.99,
    "isShippingRequired": False,
    "categoryIds": [
        115703546
    ],
    "weight": 10,
    "enabled": True,
    "description": "A <b>new</b> product description",
}

print(json.dumps(ecwid.create_product(product), indent=2))