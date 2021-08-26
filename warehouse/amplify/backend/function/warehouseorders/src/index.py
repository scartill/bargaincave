import os
import traceback
import json

from rest import make_response
from secrets import get_sm_secret
from ecwid import EcwidAPI

ECWID_SECRET_NAME = 'ecwid_api'


def item_line(item):
    name = item['name']
    quantity = item['quantity']
    return f'<li>{name} x {quantity}</li>'


def order_section(order):
    items = '\n'.join(item_line(item) for item in order['items'])
    pickup = '&nbsp;(самовывоз)' if order['shippingOption']['isPickup'] else ''

    return f"""
        <h2>{order['id'][:-1]}{pickup}</h2>
        <ol>{items}</ol>
    """


def generate_report(api):
    params = {
        'fulfillmentStatus': 'PROCESSING'
    }

    orders = api.orders(params)
    order_sections = '<br/>'.join(order_section(order) for order in orders['items'])

    content = f"""
<html>
    <head>
        <meta charset="utf-8">
        <link
            rel="stylesheet"
            href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
            integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
            crossorigin="anonymous"/>
    </head>
    <body>
        <div class="container">
            <h1>Заказы</h1><br/>
            {order_sections}
        </div>
    </body>
</html>
    """

    response = {
        'statusCode': 200,
        "body": content,
        "headers": {
            'Content-Type': 'text/html'
        }
    }

    return response


def process_request():
    ecwid_tokens = json.loads(
        get_sm_secret(f"{ECWID_SECRET_NAME}-{os.getenv('ENV')}")
    )

    api = EcwidAPI(
        os.getenv('ECWID_DOMAIN'),
        ecwid_tokens['StoreID'],
        ecwid_tokens['Public'],
        ecwid_tokens['Secret']
    )

    return generate_report(api)


def handler(event, context):
    print('EVENT', event)
    try:
        return process_request()
    except Exception as e:
        traceback.print_exc()
        return make_response(error=e)
