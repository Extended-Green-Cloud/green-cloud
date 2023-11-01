import { ResourceCharacteristic } from "./resource-characteristic-type";

export type ResourceCharacteristics = { [key: string]: ResourceCharacteristic };

export interface Resource {
	characteristics: ResourceCharacteristics;
	emptyResource: Resource;
	sufficiencyValidator: string;
	resourceComparator: string;
}
