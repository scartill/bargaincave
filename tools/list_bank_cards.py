import json
import requests
import sys

r = requests.get(
    'https://robot.dostavista.ru/api/business/1.1/bank-cards',
    headers={
        'X-DV-Auth-Token': sys.argv[1]
     }
)

print(json.dumps(r.json(), indent=2))
