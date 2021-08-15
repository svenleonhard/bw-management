import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { IItem } from 'app/shared/model/item.model';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ContentService } from '../content/content.service';
import { ItemService } from './item.service';

@Component({
  selector: 'jhi-item-child',
  templateUrl: './item-child.component.html',
})
export class ItemChildComponent implements OnInit {
  item: IItem | null = null;
  subItems: (IItem | undefined)[] | null = null;
  picture: any | null = null;

  constructor(
    protected activatedRoute: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private itemService: ItemService,
    private contentService: ContentService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data
      .pipe(
        switchMap(res => {
          this.item = res.item;
          if (this.item && this.item.picture) {
            const objectURL = 'data:image/jpeg;base64,' + this.item.picture.data;
            this.picture = this.sanitizer.bypassSecurityTrustUrl(objectURL);
          }
          if (this.item) {
            return this.itemService.query({ 'parentId.equals': this.item.id });
          }
          return new Observable<HttpResponse<IItem[]>>();
        })
      )
      .pipe(
        switchMap(res => {
          this.subItems = res.body;
          return this.contentService.query({ 'itemId.equals': this.item?.id });
        })
      )
      .subscribe(res => {
        if (this.item) {
          this.item.contents = res.body || undefined;
        }
      });
  }

  previousState(): void {
    window.history.back();
  }
}
