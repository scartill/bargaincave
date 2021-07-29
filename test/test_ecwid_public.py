import os
import json

from ecwid import EcwidAPI

ecwid = EcwidAPI(
    os.getenv('ECWID_DOMAIN'),
    os.getenv('ECWID_STORE'),
    os.getenv('ECWID_PUBLIC'),
    os.getenv('ECWID_SECRET')
)

cs = ecwid.categories()['items']
[print(c['id'], c['name']) for c in cs]

print('Delivery order')
order = ecwid.order('8IFOE')
print(json.dumps(order, indent=2))

print('Self-pick order')
order = ecwid.order('PIZUW')
print(json.dumps(order, indent=2))
