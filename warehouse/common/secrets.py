from functools import lru_cache

import boto3


@lru_cache
def get_sm_secret(name):
    client = boto3.client('secretsmanager')
    get_secret_value_response = client.get_secret_value(
        SecretId=name
    )
    return get_secret_value_response['SecretString']
