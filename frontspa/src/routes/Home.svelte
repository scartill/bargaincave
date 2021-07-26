<script>
    import GDPR from '../components/GDPR.svelte';

	let version = "Version: " + process.env.BUILD_ENV
</script>

<h1>VkusB Portal</h1>
<p><a href="https://store.vkusb.ru">VkusB Online Store</p>
<p><a href="https://t.me/joinchat/91prONyOBJc4MTIy">Join VkusB Telegram channel</a> to receive new lot announcements</p>
<p class='grey' contenteditable="false" bind:textContent={version}/>

<GDPR/>

<style>
	h1 {
		color: #ff00ffff;
		text-transform: uppercase;
		font-size: 4em;
		font-weight: 100;
	}

	p.grey {
		color: #CCCCCC;
	}
</style>
