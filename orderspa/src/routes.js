import Home from './routes/Home.svelte';
import Order from './routes/Order.svelte';
import NotFound from './routes/NotFound.svelte';

export default {
    '/': Home,
    '/order/:lot_id': Order,
    // The catch-all route must always be last
    '*': NotFound
};
