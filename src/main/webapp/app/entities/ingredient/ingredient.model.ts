import { IMenu } from 'app/entities/menu/menu.model';

export interface IIngredient {
  id: number;
  name?: string | null;
  menus?: Pick<IMenu, 'id'>[] | null;
}

export type NewIngredient = Omit<IIngredient, 'id'> & { id: null };
