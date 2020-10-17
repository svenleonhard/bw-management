import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ItemService } from '../entities/item/item.service';

@Component({
  selector: 'jhi-qr-scanner',
  templateUrl: './qr-scanner.component.html',
  styleUrls: ['./qr-scanner.component.scss'],
})
export class QrScannerComponent implements OnInit {
  @Output()
  itemCodeSelected = new EventEmitter<number>();

  itemId = undefined;

  constructor(private itemService: ItemService) {}

  onCodeResult(qrString: string): any {
    this.itemCodeSelected.emit(parseInt(qrString, 10));
  }

  onCodeSelected(): any {
    if (this.itemId) {
      this.itemCodeSelected.emit(this.itemId);
    }
  }

  ngOnInit(): void {}
}
