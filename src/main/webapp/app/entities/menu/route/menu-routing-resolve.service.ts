import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenu } from '../menu.model';
import { MenuService } from '../service/menu.service';

@Injectable({ providedIn: 'root' })
export class MenuRoutingResolveService implements Resolve<IMenu | null> {
  constructor(protected service: MenuService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMenu | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((menu: HttpResponse<IMenu>) => {
          if (menu.body) {
            return of(menu.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
