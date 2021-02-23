import App from './App.svelte';

// or if you don't want to install all the categories
import Amplify, { API } from 'aws-amplify';
import aws_exports from './aws-exports';

// in this way you are only importing Auth and configuring it.
Amplify.configure(aws_exports);

const app = new App({
	target: document.body
});

export default app;
