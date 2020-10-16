import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BwManagementSharedModule } from 'app/shared/shared.module';
import { LettingComponent } from './letting.component';
import { LettingDetailComponent } from './letting-detail.component';
import { LettingUpdateComponent } from './letting-update.component';
import { LettingDeleteDialogComponent } from './letting-delete-dialog.component';
import { lettingRoute } from './letting.route';

@NgModule({
  imports: [BwManagementSharedModule, RouterModule.forChild(lettingRoute)],
  declarations: [LettingComponent, LettingDetailComponent, LettingUpdateComponent, LettingDeleteDialogComponent],
  entryComponents: [LettingDeleteDialogComponent],
})
export class BwManagementLettingModule {}
