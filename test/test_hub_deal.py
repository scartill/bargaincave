from warehouse.amplify.backend.function.hubspotdeal.src.index import hubspot_deal_create

payload = {
    'lot_id': 'c9754566-0d2b-4f8d-abbe-e729ea17d63a',
    'name': 'John Doe',
    'phone': '+79991234567'
}

hubspot_deal_create(payload)