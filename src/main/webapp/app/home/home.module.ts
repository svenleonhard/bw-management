import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BwManagementSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { ZXingScannerModule } from '@zxing/ngx-scanner';

@NgModule({
  imports: [BwManagementSharedModule, RouterModule.forChild([HOME_ROUTE]), ZXingScannerModule],
  declarations: [HomeComponent],
})
export class BwManagementHomeModule {}
