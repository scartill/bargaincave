import json

from telegram import Bot
from telegram.utils import helpers

BOT_API_KEY = "1604602953:AAFoaCe-CCSqbnX61zVcVIbg-I4bGgFAX8k"  # Bargain Cave Bot
CHANNEL_ID = '-1001202477347'   # Bargain Cave Staging

LOT_ID = '2f96753c-70e0-4711-a984-7d9fe86cba14'
LOT_PHOTO = 'https://bargaincave-photos142503-staging.s3.eu-west-2.amazonaws.com/public/lot_photo_1307507264875693089.jpg'

# Parameters: event, context
def handler(event, _):
    body = json.loads(event['body'])
    content = body['bot_message_contents']

    print('built content:')
    print(content)

    bot = Bot(BOT_API_KEY)
    deep_url = helpers.create_deep_linked_url(bot.username, LOT_ID)
    caption = f'{content}: {deep_url}'
    message = bot.send_photo(
        chat_id=CHANNEL_ID,
        photo=LOT_PHOTO,
        caption=caption
    )

    return_code = 503
    if message:
        return_code = 200

    return {
        'statusCode': return_code,
        'headers': {
            'Access-Control-Allow-Headers': '*',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
        },
        'body': "executed"
    }
