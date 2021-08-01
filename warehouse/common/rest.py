import json


def make_response(response=None, error=None):
    if error:
        return_code = 503
        body = {
            'result': 'error',
            'error': str(error)
        }
    else:
        return_code = 200
        body = {
            'result': 'ok'
        }

        if response:
            body['data'] = response

    body_string = json.dumps(body)

    return {
        'statusCode': return_code,
        'body': body_string
    }


def norm_headers(headers):
    return {k.lower(): v for (k, v) in headers.items()}
