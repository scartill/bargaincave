<script>
    import Amplify, { API } from 'aws-amplify';
    import GDPR from '../components/GDPR.svelte';

    export let params = {};
    let status = 'init';

    let name = '';
    let phone = '';

    function submitClick() {
        status = 'submitting'

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
                status = 'success'
            })
            .catch(error => {
                console.log(error.response);
                status = 'error'
            });
    }
</script>

<h1>Bargain Cave Order</h1>

{#if status == 'init'}
    <div>
        <label for="fname">Name:</label>
        <input id="fname" bind:value={name}/><br/>
        <br/>
        <label for="fphone">Phone:</label>
        <input id="fphone" bind:value={phone}/><br/>
        <br/>
        <button on:click={submitClick}>Submit</button>
    </div>
{/if}

{#if status == 'submitting'}
    <div>
        <p>Submitting, please wait...</p>
    </div>
{/if}

{#if status == 'success'}
    <div>
        <p>Thank you for your order! We will call you to agree on delivery or pickup.</p>
    </div>
{/if}

{#if status == 'error'}
    <div>
        <p>System error. We are sorry for inconvenience.</p>
    </div>
{/if}

<p>&copy; Bargain Cave, 2021</p>

<GDPR/>

<style>
	h1 {
		color: #ff00ffff;
		text-transform: uppercase;
		font-size: 2em;
		font-weight: 100;
	}
</style>
