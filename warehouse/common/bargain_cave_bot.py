import os
import traceback

from telegram import Bot

from secrets import get_sm_secret

TELEGRAM_BOT_TOKEN_SECRET_NAME = "telegram_token"


def get_bot_client():
    bot_token = get_sm_secret(f"{TELEGRAM_BOT_TOKEN_SECRET_NAME}-{os.getenv('ENV')}")
    return Bot(bot_token)


def service_message(text):
    # This must always succeed
    try:
        bot = get_bot_client()
        group_id = os.getenv('SERVICE_GROUP_ID')
        bot.send_message(
            chat_id=group_id,
            text=text
        )
    except Exception as e:
        print(f'Unable to send service message {text} ({e})')
        traceback.print_exc()
