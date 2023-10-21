import { IRestaurant, NewRestaurant } from './restaurant.model';

export const sampleWithRequiredData: IRestaurant = {
  id: 59197,
  name: 'zero c',
};

export const sampleWithPartialData: IRestaurant = {
  id: 63777,
  name: 'Keyboard',
  description: 'expedite',
};

export const sampleWithFullData: IRestaurant = {
  id: 5345,
  name: 'base',
  description: 'tan',
  adress: 'Technicien Saint-Bernard',
  rating: 58782,
};

export const sampleWithNewData: NewRestaurant = {
  name: 'Manager Operative',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
