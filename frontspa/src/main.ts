import App from './App.svelte';

// Uncomment for AWS support
// import Amplify, { API } from 'aws-amplify';
// import aws_exports from './aws-exports';
// Amplify.API.configure(aws_exports);

const app = new App({
	target: document.body
});

export default app;
