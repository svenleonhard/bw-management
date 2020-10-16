import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IAssignment, Assignment } from 'app/shared/model/assignment.model';
import { AssignmentService } from './assignment.service';
import { AssignmentComponent } from './assignment.component';
import { AssignmentDetailComponent } from './assignment-detail.component';
import { AssignmentUpdateComponent } from './assignment-update.component';

@Injectable({ providedIn: 'root' })
export class AssignmentResolve implements Resolve<IAssignment> {
  constructor(private service: AssignmentService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAssignment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((assignment: HttpResponse<Assignment>) => {
          if (assignment.body) {
            return of(assignment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Assignment());
  }
}

export const assignmentRoute: Routes = [
  {
    path: '',
    component: AssignmentComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.assignment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AssignmentDetailComponent,
    resolve: {
      assignment: AssignmentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.assignment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AssignmentUpdateComponent,
    resolve: {
      assignment: AssignmentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.assignment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AssignmentUpdateComponent,
    resolve: {
      assignment: AssignmentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'bwManagementApp.assignment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
