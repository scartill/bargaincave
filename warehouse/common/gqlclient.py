import json

import requests
#from requests_aws4auth import AWS4Auth


class GQLException(Exception):
    pass


class GQLClient:
    def __init__(self, endpoint: str,
        api_key: str=None,
        iam_access_key: str=None,
        iam_access_secret: str=None
    ):
        self.endpoint = endpoint
        self.api_key = api_key
        self.iam_access_key = iam_access_key
        self.iam_access_secret = iam_access_secret

    def execute(self, query: str, variables={}):
        payload = {
            'query': query,
            'variables': variables
        }

        if self.api_key:
            headers = {
                'x-api-key': self.api_key
            }

            response = requests.post(self.endpoint, headers=headers, json=payload)
        else:
            session = requests.Session()

            session.auth = AWS4Auth(
                self.iam_access_key,
                self.iam_access_secret,
                'eu-west-2',
                'appsync'
            )

            response = session.request(url=self.endpoint, method='POST', json=payload)

        response.raise_for_status()
        result = response.json()

        if 'errors' in result:
            message = json.dumps(result['errors'], indent=2)
            raise GQLException(message)

        return result['data']
