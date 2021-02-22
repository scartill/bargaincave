import os
import json

import boto3

from telegram import Bot
from telegram.utils import helpers

TELEGRAM_BOT_TOKEN_SECRET_NAME = "telegram_token"

LOT_ID = '2f96753c-70e0-4711-a984-7d9fe86cba14'
LOT_PHOTO = 'https://bargaincave-photos142503-staging.s3.eu-west-2.amazonaws.com/public/lot_photo_1307507264875693089.jpg'

# Parameters: event, context
def handler(event, _):
    return_code = 503

    try:
        body = json.loads(event['body'])
        content = body['bot_message_contents']

        sm = boto3.client('secretsmanager')
        secret_value_response = sm.get_secret_value(
            SecretId=TELEGRAM_BOT_TOKEN_SECRET_NAME
        )
        bot_token = secret_value_response['SecretString']

        channel_id = os.environ['CHANNEL_ID']

        bot = Bot(bot_token)
        deep_url = helpers.create_deep_linked_url(bot.username, LOT_ID)
        caption = f'{content}: {deep_url}'
        message = bot.send_photo(
            chat_id=channel_id,
            photo=LOT_PHOTO,
            caption=caption
        )

        if message:
            return_code = 200
    except Exception as e:
        print(f'Telegram exception {e}')


    return {
        'statusCode': return_code,
        'headers': {
            'Access-Control-Allow-Headers': '*',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
        },
        'body': "executed"
    }
