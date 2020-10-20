import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';

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

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;
  item: IItem | null = null;
  subItems: (IItem | undefined)[] | null = null;
  picture: any | null = null;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private itemService: ItemService,
    private sanitizer: DomSanitizer,
    private assignmentService: AssignmentService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  itemCodeSelected(itemCode: number): void {
    this.assignmentService.query({ 'boxId.equals': itemCode }).subscribe(res => {
      console.log('read assignments');
      console.log(res.body);

      if (res.body != null) {
        this.subItems = res.body.map(assignment => assignment.boxItem).filter(boxItem => boxItem != undefined);

        this.subItems = this.subItems.map(subItem => {
          if (subItem && subItem.picture) {
            subItem.picture.data = this.sanitizer.bypassSecurityTrustUrl('data:image/jpeg;base64,' + subItem.picture.data);
          }
          return subItem;
        });
        console.log(this.subItems);
      }
    });

    this.itemService.find(itemCode).subscribe(res => {
      this.item = res.body || null;
      if (this.item && this.item.picture) {
        const objectURL = 'data:image/jpeg;base64,' + this.item.picture.data;
        this.picture = this.sanitizer.bypassSecurityTrustUrl(objectURL);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
