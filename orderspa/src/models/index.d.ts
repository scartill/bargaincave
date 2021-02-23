import { ModelInit, MutableModel, PersistentModelConstructor } from "@aws-amplify/datastore";





export declare class Lot {
  readonly id: string;
  readonly weightKg?: number;
  readonly comment?: string;
  readonly photo?: string;
  readonly fruit?: string;
  constructor(init: ModelInit<Lot>);
  static copyOf(source: Lot, mutator: (draft: MutableModel<Lot>) => MutableModel<Lot> | void): Lot;
}