import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IContent, Content } from 'app/shared/model/content.model';
import { ContentService } from './content.service';
import { IItem } from 'app/shared/model/item.model';
import { ItemService } from 'app/entities/item/item.service';

@Component({
  selector: 'jhi-content-update',
  templateUrl: './content-update.component.html',
})
export class ContentUpdateComponent implements OnInit {
  isSaving = false;
  items: IItem[] = [];

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required]],
    item: [],
  });

  constructor(
    protected contentService: ContentService,
    protected itemService: ItemService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ content }) => {
      this.updateForm(content);

      this.itemService.query().subscribe((res: HttpResponse<IItem[]>) => (this.items = res.body || []));
    });
  }

  updateForm(content: IContent): void {
    this.editForm.patchValue({
      id: content.id,
      description: content.description,
      item: content.item,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const content = this.createFromForm();
    if (content.id !== undefined) {
      this.subscribeToSaveResponse(this.contentService.update(content));
    } else {
      this.subscribeToSaveResponse(this.contentService.create(content));
    }
  }

  private createFromForm(): IContent {
    return {
      ...new Content(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      item: this.editForm.get(['item'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IContent>>): void {
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

  trackById(index: number, item: IItem): any {
    return item.id;
  }
}
