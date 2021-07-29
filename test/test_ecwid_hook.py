import os
import json
from pathlib import Path

from ecwid import EcwidWebhook


client_secret = os.getenv('ECWID_CLIENT')
ew = EcwidWebhook(client_secret)


def test_new_order():
    payload = json.loads(Path('data/ecwid_new_order_hook.json').read_text())
    print(ew.process(payload))


def test_updated_order():
    payload = json.loads(Path('data/ecwid_updated_order_hook.json').read_text())
    print(ew.process(payload))

test_new_order()
test_updated_order()
