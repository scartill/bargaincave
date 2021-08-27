import requests


class DadataAPI:
    def __init__(self, domain, token, secret):
        self.headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Token {token}',
            'X-Secret': secret
        }

        self.url = f'https://{domain}/api/v1/clean/address'

    def clean_address(self, address):
        payload = [address]

        r = requests.post(self.url, json=payload, headers=self.headers)
        r.raise_for_status()
        data = r.json()

        print('Dadata :: Address', data[0]['result'])

        return {
            'address': data[0]['result'],
            'is_check_required': data[0]['qc_geo'] != 0,
            'remainder': data[0]['unparsed_parts']
        }
