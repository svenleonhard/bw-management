import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILetting } from 'app/shared/model/letting.model';
import { LettingService } from './letting.service';
import { LettingDeleteDialogComponent } from './letting-delete-dialog.component';

@Component({
  selector: 'jhi-letting',
  templateUrl: './letting.component.html',
})
export class LettingComponent implements OnInit, OnDestroy {
  lettings?: ILetting[];
  eventSubscriber?: Subscription;

  constructor(protected lettingService: LettingService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.lettingService.query().subscribe((res: HttpResponse<ILetting[]>) => (this.lettings = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInLettings();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ILetting): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInLettings(): void {
    this.eventSubscriber = this.eventManager.subscribe('lettingListModification', () => this.loadAll());
  }

  delete(letting: ILetting): void {
    const modalRef = this.modalService.open(LettingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.letting = letting;
  }
}
