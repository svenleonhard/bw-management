import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BwManagementSharedModule } from 'app/shared/shared.module';
import { AssignmentComponent } from './assignment.component';
import { AssignmentDetailComponent } from './assignment-detail.component';
import { AssignmentUpdateComponent } from './assignment-update.component';
import { AssignmentDeleteDialogComponent } from './assignment-delete-dialog.component';
import { assignmentRoute } from './assignment.route';

@NgModule({
  imports: [BwManagementSharedModule, RouterModule.forChild(assignmentRoute)],
  declarations: [AssignmentComponent, AssignmentDetailComponent, AssignmentUpdateComponent, AssignmentDeleteDialogComponent],
  entryComponents: [AssignmentDeleteDialogComponent],
})
export class BwManagementAssignmentModule {}
