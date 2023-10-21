import { IMenu } from 'app/entities/menu/menu.model';
import { Tag } from 'app/entities/enumerations/tag.model';

export interface ITag {
  id: number;
  name?: Tag | null;
  menus?: Pick<IMenu, 'id'>[] | null;
}

export type NewTag = Omit<ITag, 'id'> & { id: null };
