import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAssignment } from 'app/shared/model/assignment.model';
import { AssignmentService } from './assignment.service';

@Component({
  templateUrl: './assignment-delete-dialog.component.html',
})
export class AssignmentDeleteDialogComponent {
  assignment?: IAssignment;

  constructor(
    protected assignmentService: AssignmentService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.assignmentService.delete(id).subscribe(() => {
      this.eventManager.broadcast('assignmentListModification');
      this.activeModal.close();
    });
  }
}
