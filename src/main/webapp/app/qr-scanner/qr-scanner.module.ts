import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QrScannerComponent } from './qr-scanner.component';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [QrScannerComponent],
  exports: [QrScannerComponent],
  imports: [CommonModule, ZXingScannerModule, FontAwesomeModule, FormsModule],
})
export class QrScannerModule {}
