// @ts-check
import { initSchema } from '@aws-amplify/datastore';
import { schema } from './schema';



const { Lot } = initSchema(schema);

export {
  Lot
};