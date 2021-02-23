<script>
    export let params = {};
    let name = ''
    let phone = ''

    function submitClick() {
        alert('Submitting ' + params.lot_id + ' ' + name + ' ' + phone) 
    }
</script>

<h1>Bargain Cave Order</h1>
<div>
    <p>Name: <input bind:value={name}/>
    <p>Phone: <input bind:value={phone}/></p>
    <button on:click={submitClick}>Submit</button>
</div>

<style>
	h1 {
		color: #ff00ffff;
		text-transform: uppercase;
		font-size: 2em;
		font-weight: 100;
	}
</style>
