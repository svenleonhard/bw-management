import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IItem } from 'app/shared/model/item.model';
import { ItemService } from 'app/entities/item/item.service';
import { DomSanitizer } from '@angular/platform-browser';
import { logger } from 'codelyzer/util/logger';
import { AssignmentService } from 'app/entities/assignment/assignment.service';
import { equals } from '@ngx-translate/core/lib/util';
import { switchMap } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FindItemModalComponent } from 'app/find-item-modal/find-item-modal.component';
import { ContentService } from 'app/entities/content/content.service';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;
  items: (IItem | undefined)[] | null = null;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private itemService: ItemService,
    private contentService: ContentService,
    private sanitizer: DomSanitizer,
    private modalService: NgbModal,
    private assignmentService: AssignmentService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));

    this.itemService.query().subscribe(itemRes => {
      this.items = itemRes.body;
      this.items?.forEach(item => {
        if (item && item.picture) {
          const objectURL = 'data:image/jpeg;base64,' + item.picture.data;
          item.picture.data = this.sanitizer.bypassSecurityTrustUrl(objectURL);
        }
        this.contentService.query({ 'itemId.equals': item?.id }).subscribe(contentRes => {
          if (item) {
            item.contents = contentRes.body || undefined;
          }
        });
      });
    });
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  openFindItemModal(): void {
    const modalRef = this.modalService.open(FindItemModalComponent);
    modalRef.componentInstance.passEntry.subscribe((itemId: any) => {});
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
