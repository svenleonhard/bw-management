import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'item',
        loadChildren: () => import('./item/item.module').then(m => m.BwManagementItemModule),
      },
      {
        path: 'content',
        loadChildren: () => import('./content/content.module').then(m => m.BwManagementContentModule),
      },
      {
        path: 'assignment',
        loadChildren: () => import('./assignment/assignment.module').then(m => m.BwManagementAssignmentModule),
      },
      {
        path: 'letting',
        loadChildren: () => import('./letting/letting.module').then(m => m.BwManagementLettingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class BwManagementEntityModule {}
