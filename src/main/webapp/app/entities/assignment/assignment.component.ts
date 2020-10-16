import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAssignment } from 'app/shared/model/assignment.model';
import { AssignmentService } from './assignment.service';
import { AssignmentDeleteDialogComponent } from './assignment-delete-dialog.component';

@Component({
  selector: 'jhi-assignment',
  templateUrl: './assignment.component.html',
})
export class AssignmentComponent implements OnInit, OnDestroy {
  assignments?: IAssignment[];
  eventSubscriber?: Subscription;

  constructor(protected assignmentService: AssignmentService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.assignmentService.query().subscribe((res: HttpResponse<IAssignment[]>) => (this.assignments = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInAssignments();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IAssignment): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInAssignments(): void {
    this.eventSubscriber = this.eventManager.subscribe('assignmentListModification', () => this.loadAll());
  }

  delete(assignment: IAssignment): void {
    const modalRef = this.modalService.open(AssignmentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.assignment = assignment;
  }
}
