from pathlib import Path
import json

from warehouse.amplify.backend.function.ecwidorder.src.index import process_paid_order

print('Self-pick order')
order = json.loads(Path('data/self_pick_order.json').read_text())
process_paid_order(order)

print('Delivery order')
order = json.loads(Path('data/delivery_order.json').read_text())
process_paid_order(order)

