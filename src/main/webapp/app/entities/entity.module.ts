import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'item',
        data: {
          authorities: [Authority.MANAGER],
        },
        canActivate: [UserRouteAccessService],
        loadChildren: () => import('./item/item.module').then(m => m.BwManagementItemModule),
      },
      {
        data: {
          authorities: [Authority.MANAGER],
        },
        canActivate: [UserRouteAccessService],
        path: 'content',
        loadChildren: () => import('./content/content.module').then(m => m.BwManagementContentModule),
      },
      {
        data: {
          authorities: [Authority.MANAGER],
        },
        canActivate: [UserRouteAccessService],
        path: 'assignment',
        loadChildren: () => import('./assignment/assignment.module').then(m => m.BwManagementAssignmentModule),
      },
      {
        data: {
          authorities: [Authority.MANAGER],
        },
        canActivate: [UserRouteAccessService],
        path: 'letting',
        loadChildren: () => import('./letting/letting.module').then(m => m.BwManagementLettingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class BwManagementEntityModule {}
