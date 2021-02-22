<script>
    export let params = {};

    function submitClick() {
        alert('Submitting')
    }
</script>

<h1>Bargain Cave Order</h1>
<p>Lot ID: {params.lot_id}</p>

<button on:click={submitClick}>Submit</button>

<style>
	h1 {
		color: #ff00ffff;
		text-transform: uppercase;
		font-size: 4em;
		font-weight: 100;
	}
</style>

