import json
import requests


class GQLException(Exception):
    pass


class GQLClient:
    def __init__(self, endpoint: str, api_key: str):
        self.endpoint = endpoint
        self.api_key = api_key

    def execute(self, query: str, variables={}):
        headers = {
            'x-api-key': self.api_key
        }

        payload = {
            'query': query,
            'variables': variables
        }

        response = requests.post(self.endpoint, headers=headers, json=payload).json()

        if 'errors' in response:
            message = json.dumps(response['errors'], indent=2)
            raise GQLException(message)

        return response['data']
