export interface IRestaurant {
  id: number;
  name?: string | null;
  description?: string | null;
  adress?: string | null;
  rating?: number | null;
}

export type NewRestaurant = Omit<IRestaurant, 'id'> & { id: null };
