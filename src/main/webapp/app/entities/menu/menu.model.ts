import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { IIngredient } from 'app/entities/ingredient/ingredient.model';
import { ITag } from 'app/entities/tag/tag.model';

export interface IMenu {
  id: number;
  name?: string | null;
  description?: string | null;
  price?: number | null;
  restaurant?: Pick<IRestaurant, 'id'> | null;
  ingredients?: Pick<IIngredient, 'id'>[] | null;
  tags?: Pick<ITag, 'id'>[] | null;
}

export type NewMenu = Omit<IMenu, 'id'> & { id: null };
