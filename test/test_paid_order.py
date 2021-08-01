import os
from pathlib import Path
import json

from warehouse.amplify.backend.function.ecwidorder.src.index import process_paid_order
from warehouse.common.dostavista import DostavistaAPI

dosta = DostavistaAPI(
    os.getenv('DOSTA_DOMAIN'),
    os.getenv('DOSTA_API_KEY')
)

dosta.set_origin(
    os.getenv('DOSTA_DISPATCH_ADDRESS'),
    os.getenv('DOSTA_DISPATCH_PHONE')
)

dosta.set_bankcard(
    os.getenv('DOSTA_BANKCARD_ID')
)

print('Self-pick order')
order = json.loads(Path('data/self_pick_order.json').read_text())
process_paid_order(order, dosta)

print('Delivery order')
order = json.loads(Path('data/delivery_order.json').read_text())
process_paid_order(order, dosta)

