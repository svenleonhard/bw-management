import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ILetting, Letting } from 'app/shared/model/letting.model';
import { LettingService } from './letting.service';
import { LettingComponent } from './letting.component';
import { LettingDetailComponent } from './letting-detail.component';
import { LettingUpdateComponent } from './letting-update.component';

@Injectable({ providedIn: 'root' })
export class LettingResolve implements Resolve<ILetting> {
  constructor(private service: LettingService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILetting> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((letting: HttpResponse<Letting>) => {
          if (letting.body) {
            return of(letting.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Letting());
  }
}

export const lettingRoute: Routes = [
  {
    path: '',
    component: LettingComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.letting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LettingDetailComponent,
    resolve: {
      letting: LettingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.letting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LettingUpdateComponent,
    resolve: {
      letting: LettingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.letting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LettingUpdateComponent,
    resolve: {
      letting: LettingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.letting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
