import os
import json
from pathlib import Path

from dostavista import DostavistaWebhook


client_secret = os.getenv('DOSTA_CALLBACK_KEY')
dw = DostavistaWebhook(client_secret)


def test_new_order():
    payload = json.loads(Path('data/dosta_order_created.json').read_text(encoding='utf-8'))
    print(dw.process(payload))


test_new_order()
