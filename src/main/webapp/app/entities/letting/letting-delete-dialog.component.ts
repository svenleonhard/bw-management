import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILetting } from 'app/shared/model/letting.model';
import { LettingService } from './letting.service';

@Component({
  templateUrl: './letting-delete-dialog.component.html',
})
export class LettingDeleteDialogComponent {
  letting?: ILetting;

  constructor(protected lettingService: LettingService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.lettingService.delete(id).subscribe(() => {
      this.eventManager.broadcast('lettingListModification');
      this.activeModal.close();
    });
  }
}
