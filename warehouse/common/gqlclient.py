import json

import requests
from requests.sessions import session
from requests_aws4auth import AWS4Auth


class GQLException(Exception):
    pass


class GQLClient:
    def __init__(self, endpoint: str,
        api_key: str=None,
        aws_region: str=None,
        iam_access_key: str=None,
        iam_access_secret: str=None,
        iam_session_token: str=None
    ):
        self.endpoint = endpoint        
        self.api_key = api_key
        self.aws_region = aws_region
        self.iam_access_key = iam_access_key
        self.iam_access_secret = iam_access_secret
        self.iam_session_token = iam_session_token

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
            auth = AWS4Auth(
                self.iam_access_key,
                self.iam_access_secret,
                self.aws_region,
                'appsync',
                session_token=self.iam_session_token
            )

            response = requests.post(self.endpoint, auth=auth, json=payload)

        response.raise_for_status()
        result = response.json()

        if 'errors' in result:
            message = json.dumps(result['errors'], indent=2)
            raise GQLException(message)

        return result['data']
