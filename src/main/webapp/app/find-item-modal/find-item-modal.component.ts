import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-find-item-modal',
  templateUrl: './find-item-modal.component.html',
  styleUrls: ['./find-item-modal.component.scss'],
})
export class FindItemModalComponent implements OnInit {
  @Output() passEntry: EventEmitter<number> = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}

  dismiss(): void {
    this.activeModal.dismiss();
  }

  itemCodeSelected(itemCode: number): void {
    this.passEntry.emit(itemCode);
    this.activeModal.dismiss();
  }
}
