import { IIngredient, NewIngredient } from './ingredient.model';

export const sampleWithRequiredData: IIngredient = {
  id: 37469,
  name: 'e-business global',
};

export const sampleWithPartialData: IIngredient = {
  id: 83397,
  name: 'Salad HDD Victoire',
};

export const sampleWithFullData: IIngredient = {
  id: 67561,
  name: 'Granite Buckinghamshire online',
};

export const sampleWithNewData: NewIngredient = {
  name: 'Gloves wireless deliverables',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
