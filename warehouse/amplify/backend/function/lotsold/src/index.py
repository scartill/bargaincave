import json
import boto3

from hubspot import HubSpot
from hubspot.crm.objects import SimplePublicObjectInput


HUBSPOT_API_TOKEN_SECRET_NAME = 'hubspot_token'


def hubspot_deal_create(params, payload):
    lot_id = params['lotid']
    print('PAYLOAD', payload)

    sm = boto3.client('secretsmanager')

    secret_value_response = sm.get_secret_value(
        SecretId=HUBSPOT_API_TOKEN_SECRET_NAME
    )

    api_key = secret_value_response['SecretString']

    hapi = HubSpot(api_key=api_key)

    simple_public_object_input = SimplePublicObjectInput(
        properties={
            'amount': '1500.00',
            'dealname': f'Lot {lot_id}',
            'dealstage': '11141448',
            'pipeline': 'default'
        }
    )
    
    response = hapi.crm.deals.basic_api.create(simple_public_object_input)
    print('HUBSPOT', response)
    # TODO: Link HubSpot deal with a lot
    return 200


def handler(event, context):  
    return_code = 503

    try:
        params = event['pathParameters']
        payload = json.loads(event['body'])
        return_code = hubspot_deal_create(params, payload)
    except Exception as e:
        print(f'Hubspot exception {e}')

    return {
        'statusCode': return_code,
        'headers': {
            'Access-Control-Allow-Headers': '*',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
        },
        'body': '{"result": "executed"}'
    }

