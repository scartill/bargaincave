import App from './App.svelte';

import Amplify, { API } from 'aws-amplify';

// TODO: generate aws-exports using Amplify Headless Mode
import aws_exports from './aws-exports';

Amplify.configure(aws_exports);

const app = new App({
	target: document.body
});

export default app;
