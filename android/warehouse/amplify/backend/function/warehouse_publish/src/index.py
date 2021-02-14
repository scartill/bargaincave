import requests

BOT_API_KEY = "1604602953:AAFoaCe-CCSqbnX61zVcVIbg-I4bGgFAX8k"
CHAT_ID = "-1001202477347"

def handler(event, context):
    print('received event:')
    print(event)

    content = event['bot_message_contents']

    print('built content:')
    print(content)

    url = f"https://api.telegram.org/bot{BOT_API_KEY}/sendMessage?chat_id={CHAT_ID}&text={content}"

    response = requests.post(url).json()

    print("response content:")
    print(response)

    return_code = 503
    if (response['ok']):
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
