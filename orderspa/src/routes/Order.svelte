<script>
    import Amplify, { API } from 'aws-amplify';

    export let params = {};
    let name = ''
    let phone = ''

    function submitClick() {
        console.log('submit', params.lot_id, name, phone) 

        const apiName = 'warehouseapi';
        const path = '/crm/deal/create';
        const post_init = {
            body: {
                lot_id: params.lot_id,
                name: name,
                phone: phone
            }
        };

        API
            .post(apiName, path, post_init)
            .then(response => {
                console.log('API call success', response)
            })
            .catch(error => {
                console.log(error.response);
            });
    }
</script>

<h1>Bargain Cave Order</h1>
<div>
    <p>Name: <input bind:value={name}/>
    <p>Phone: <input bind:value={phone}/></p>
    <button on:click={submitClick}>Submit</button>
    <p>(c) Bargain Cave, 2021</p>
</div>

<style>
	h1 {
		color: #ff00ffff;
		text-transform: uppercase;
		font-size: 2em;
		font-weight: 100;
	}
</style>
