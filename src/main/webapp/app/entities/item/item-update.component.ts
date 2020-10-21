import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IItem, Item } from 'app/shared/model/item.model';
import { ItemService } from './item.service';
import { IImage } from 'app/shared/model/image.model';
import { ImageService } from 'app/entities/image/image.service';

type SelectableEntity = IImage | IItem;

@Component({
  selector: 'jhi-item-update',
  templateUrl: './item-update.component.html',
})
export class ItemUpdateComponent implements OnInit {
  isSaving = false;
  pictures: IImage[] = [];
  items: IItem[] = [];

  editForm = this.fb.group({
    id: [],
    qrCode: [null, [Validators.required]],
    description: [null, [Validators.required]],
    picture: [],
    parent: [],
  });

  constructor(
    protected itemService: ItemService,
    protected imageService: ImageService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ item }) => {
      this.updateForm(item);

      this.imageService
        .query({ 'itemId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IImage[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IImage[]) => {
          if (!item.picture || !item.picture.id) {
            this.pictures = resBody;
          } else {
            this.imageService
              .find(item.picture.id)
              .pipe(
                map((subRes: HttpResponse<IImage>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IImage[]) => (this.pictures = concatRes));
          }
        });

      this.itemService.query().subscribe((res: HttpResponse<IItem[]>) => (this.items = res.body || []));
    });
  }

  updateForm(item: IItem): void {
    this.editForm.patchValue({
      id: item.id,
      qrCode: item.qrCode,
      description: item.description,
      picture: item.picture,
      parent: item.parent,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const item = this.createFromForm();
    if (item.id !== undefined) {
      this.subscribeToSaveResponse(this.itemService.update(item));
    } else {
      this.subscribeToSaveResponse(this.itemService.create(item));
    }
  }

  private createFromForm(): IItem {
    return {
      ...new Item(),
      id: this.editForm.get(['id'])!.value,
      qrCode: this.editForm.get(['qrCode'])!.value,
      description: this.editForm.get(['description'])!.value,
      picture: this.editForm.get(['picture'])!.value,
      parent: this.editForm.get(['parent'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IItem>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
