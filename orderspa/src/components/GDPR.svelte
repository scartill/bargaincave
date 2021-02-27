<script>
    var showFull = false;

    function toggle() {
        showFull = !showFull;
    }
</script>

{#if showFull}
<p on:click={toggle}>Bargain Cave is committed to ensuring the security and protection of the personal information that we process, and to provide a compliant and consistent approach to data protection. If you have any questions related to our GDPR compliance, please contact our Data Protection Officer or make a Data Subject Access Request.</p>
{:else}
<!-- svelte-ignore a11y-missing-attribute -->
<a role="button" on:click={toggle}>Data Protection</a>
{/if}
