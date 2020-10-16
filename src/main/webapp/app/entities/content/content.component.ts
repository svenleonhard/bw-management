import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IContent } from 'app/shared/model/content.model';
import { ContentService } from './content.service';
import { ContentDeleteDialogComponent } from './content-delete-dialog.component';

@Component({
  selector: 'jhi-content',
  templateUrl: './content.component.html',
})
export class ContentComponent implements OnInit, OnDestroy {
  contents?: IContent[];
  eventSubscriber?: Subscription;

  constructor(protected contentService: ContentService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.contentService.query().subscribe((res: HttpResponse<IContent[]>) => (this.contents = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInContents();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IContent): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInContents(): void {
    this.eventSubscriber = this.eventManager.subscribe('contentListModification', () => this.loadAll());
  }

  delete(content: IContent): void {
    const modalRef = this.modalService.open(ContentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.content = content;
  }
}
