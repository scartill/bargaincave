import os
import json
import re

import boto3

from hubspot import HubSpot
from hubspot.crm.objects import SimplePublicObjectInput, PublicObjectSearchRequest, Filter, FilterGroup

from gqlclient import GQLClient  # type: ignore


PRICE_QUERY = '''
    query GetLotQuery($lotID:ID!) {
        getLot(id: $lotID) {
            id
            fruit
            price
        }
    }
'''

HUBSPOT_API_TOKEN_SECRET_NAME = 'hubspot_token'


def get_lot(lot_id):
    url = os.getenv('API_WAREHOUSE_GRAPHQLAPIENDPOINTOUTPUT')
    api_key = os.getenv('API_WAREHOUSE_GRAPHQLAPIKEYOUTPUT')

    cli = GQLClient(url, api_key)
    r = cli.execute(PRICE_QUERY, variables={
        'lotID' : lot_id
    })

    return r['getLot']


def get_hs_client():
    sm = boto3.client('secretsmanager')

    secret_value_response = sm.get_secret_value(
        SecretId=HUBSPOT_API_TOKEN_SECRET_NAME
    )

    api_key = secret_value_response['SecretString']

    return HubSpot(api_key=api_key)


def hubspot_contact_ensure(hapi, name, phone):
    filter = Filter(
        property_name="mobilephone",
        operator="EQ",
        value=phone,
    )

    filter_group = FilterGroup(filters=[filter])

    posr = PublicObjectSearchRequest(
        filter_groups=[filter_group],
    )

    contacts_page = hapi.crm.contacts.search_api.do_search(
        public_object_search_request=posr
    )

    for contact in contacts_page.results:
        return contact

    new_contact = SimplePublicObjectInput(
        properties={
            'firstname': name,
            'mobilephone': phone
        }
    )

    return hapi.crm.contacts.basic_api.create(new_contact)


def hubspot_deal_create(payload):
    lot_id = payload['lot_id']
    name = payload['name']
    phone = payload['phone']

    lot = get_lot(lot_id)

    hapi = get_hs_client()

    new_deal = SimplePublicObjectInput(
        properties={
            'amount': str(lot['price']),
            'dealname': f'Order for {lot["fruit"]}',
            'lot_id': lot['id'],
            'dealstage': '11141448',
            'pipeline': 'default'
        }
    )
    deal = hapi.crm.deals.basic_api.create(new_deal)

    contact = hubspot_contact_ensure(hapi, name, phone)

    assoc = hapi.crm.deals.associations_api.create(deal.id, 'contact', contact.id, 3)

    return 200


def make_response(return_code):
    return {
        'statusCode': return_code,
        'headers': {
            'Access-Control-Allow-Headers': '*',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
        },
        'body': '{"result": "executed"}'
    }


def handler(event, context):
    if event['httpMethod'] == 'OPTIONS':
        return make_response(200)

    return_code = 503

    try:
        # Unused for 'create' method
        # params = event['pathParameters']
        payload = json.loads(event['body'])
        return_code = hubspot_deal_create(payload)
    except Exception as e:
        print(f'Hubspot exception {e}')

    return make_response(return_code)
