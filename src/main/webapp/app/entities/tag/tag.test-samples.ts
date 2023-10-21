import { Tag } from 'app/entities/enumerations/tag.model';

import { ITag, NewTag } from './tag.model';

export const sampleWithRequiredData: ITag = {
  id: 42372,
};

export const sampleWithPartialData: ITag = {
  id: 26068,
};

export const sampleWithFullData: ITag = {
  id: 49254,
  name: Tag['PROMO'],
};

export const sampleWithNewData: NewTag = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
