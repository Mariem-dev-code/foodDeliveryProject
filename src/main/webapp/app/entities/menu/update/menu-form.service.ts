import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMenu, NewMenu } from '../menu.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMenu for edit and NewMenuFormGroupInput for create.
 */
type MenuFormGroupInput = IMenu | PartialWithRequiredKeyOf<NewMenu>;

type MenuFormDefaults = Pick<NewMenu, 'id' | 'ingredients' | 'tags'>;

type MenuFormGroupContent = {
  id: FormControl<IMenu['id'] | NewMenu['id']>;
  name: FormControl<IMenu['name']>;
  description: FormControl<IMenu['description']>;
  price: FormControl<IMenu['price']>;
  restaurant: FormControl<IMenu['restaurant']>;
  ingredients: FormControl<IMenu['ingredients']>;
  tags: FormControl<IMenu['tags']>;
};

export type MenuFormGroup = FormGroup<MenuFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MenuFormService {
  createMenuFormGroup(menu: MenuFormGroupInput = { id: null }): MenuFormGroup {
    const menuRawValue = {
      ...this.getFormDefaults(),
      ...menu,
    };
    return new FormGroup<MenuFormGroupContent>({
      id: new FormControl(
        { value: menuRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(menuRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(menuRawValue.description),
      price: new FormControl(menuRawValue.price, {
        validators: [Validators.required],
      }),
      restaurant: new FormControl(menuRawValue.restaurant),
      ingredients: new FormControl(menuRawValue.ingredients ?? []),
      tags: new FormControl(menuRawValue.tags ?? []),
    });
  }

  getMenu(form: MenuFormGroup): IMenu | NewMenu {
    return form.getRawValue() as IMenu | NewMenu;
  }

  resetForm(form: MenuFormGroup, menu: MenuFormGroupInput): void {
    const menuRawValue = { ...this.getFormDefaults(), ...menu };
    form.reset(
      {
        ...menuRawValue,
        id: { value: menuRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MenuFormDefaults {
    return {
      id: null,
      ingredients: [],
      tags: [],
    };
  }
}
