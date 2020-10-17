import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BwManagementSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { QrScannerModule } from 'app/qr-scanner/qr-scanner.module';

@NgModule({
  imports: [BwManagementSharedModule, RouterModule.forChild([HOME_ROUTE]), ZXingScannerModule, QrScannerModule],
  declarations: [HomeComponent],
})
export class BwManagementHomeModule {}
