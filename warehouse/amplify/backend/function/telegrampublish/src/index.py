import os
import json
import traceback

import boto3

from telegram import Bot, InputMediaPhoto

TELEGRAM_BOT_TOKEN_SECRET_NAME = "telegram_token"

from gqlclient import GQLClient  # type: ignore
from rest_response import make_response  # type: ignore


PRICE_QUERY = '''
    query GetLotQuery($lotID:ID!) {
        getLot(id: $lotID) {
            id
            fruit
            totalWeightKg
            pricePerPallet
            resources
        }
    }
'''

FRUIT_TRANSLATE = {
    "RU": {
        "Mango": "Манго",
        "Avocado": "Авокадо"
    }
}

ANNOUNCE_TEMPLATE = 'Продается {totalWeightKg} килограммов {fruitLocal} по цене {pricePerPallet} ₽ за коробку.'


def get_lot(lot_id):
    url = os.getenv('API_WAREHOUSE_GRAPHQLAPIENDPOINTOUTPUT')
    api_key = os.getenv('API_WAREHOUSE_GRAPHQLAPIKEYOUTPUT')

    cli = GQLClient(url, api_key)
    r = cli.execute(PRICE_QUERY, variables={
        'lotID' : lot_id
    })

    return r['getLot']


def get_bot_client():
    sm = boto3.client('secretsmanager')
    secret_value_response = sm.get_secret_value(
        SecretId=f"{TELEGRAM_BOT_TOKEN_SECRET_NAME}-{os.getenv('ENV')}"
    )
    bot_token = secret_value_response['SecretString']

    return Bot(bot_token)


def get_photo_url(photo_key):
    bucket_name = os.getenv('STORAGE_PHOTOS_BUCKETNAME')
    full_key = f'public/{photo_key}'
    region = os.getenv('REGION')
    object_url = f'http://{bucket_name}.s3.{region}.amazonaws.com/{full_key}'

    return object_url


def telegram_api_command(payload):
    if 'lot_by_id' not in payload:
        print(f'Unsupported payload {payload}')
        return 503

    lot_id = payload['lot_by_id']
    lot = get_lot(lot_id)

    resources = json.loads(lot['resources'])
    photo_keys = resources['photos']
    media = [InputMediaPhoto(get_photo_url(k['photoFile'])) for k in photo_keys]

    lot["fruitLocal"] = FRUIT_TRANSLATE["RU"][lot["fruit"]]

    channel_id = os.getenv('CHANNEL_ID')

    bot = get_bot_client()
    message = bot.send_media_group(
        chat_id=channel_id,
        media=media
    )

    if not message:
        raise RuntimeError('Cannot send telegram album')

    app_host = os.getenv('APP_HOST')
    deep_url = f'https://{app_host}/#/order/{lot_id}'
    announce = ANNOUNCE_TEMPLATE.format(**lot)
    text = f'{announce}: {deep_url}'

    message = bot.send_message(
        chat_id=channel_id,
        text=text
    )

    if not message:
        raise RuntimeError('Cannot send telegram message')


# Parameters: event, context
def handler(event, _):
    try:
        payload = json.loads(event['body'])
        telegram_api_command(payload)

        return make_response()
    except Exception as e:
        print(e)
        traceback.print_exc()
        return make_response(error=e)

