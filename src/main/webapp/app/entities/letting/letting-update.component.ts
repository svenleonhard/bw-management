import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ILetting, Letting } from 'app/shared/model/letting.model';
import { LettingService } from './letting.service';
import { IItem } from 'app/shared/model/item.model';
import { ItemService } from 'app/entities/item/item.service';

@Component({
  selector: 'jhi-letting-update',
  templateUrl: './letting-update.component.html',
})
export class LettingUpdateComponent implements OnInit {
  isSaving = false;
  items: IItem[] = [];
  startDateDp: any;
  endDateDp: any;

  editForm = this.fb.group({
    id: [],
    startDate: [null, [Validators.required]],
    endDate: [],
    name: [],
    location: [],
    item: [],
  });

  constructor(
    protected lettingService: LettingService,
    protected itemService: ItemService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ letting }) => {
      this.updateForm(letting);

      this.itemService.query().subscribe((res: HttpResponse<IItem[]>) => (this.items = res.body || []));
    });
  }

  updateForm(letting: ILetting): void {
    this.editForm.patchValue({
      id: letting.id,
      startDate: letting.startDate,
      endDate: letting.endDate,
      name: letting.name,
      location: letting.location,
      item: letting.item,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const letting = this.createFromForm();
    if (letting.id !== undefined) {
      this.subscribeToSaveResponse(this.lettingService.update(letting));
    } else {
      this.subscribeToSaveResponse(this.lettingService.create(letting));
    }
  }

  private createFromForm(): ILetting {
    return {
      ...new Letting(),
      id: this.editForm.get(['id'])!.value,
      startDate: this.editForm.get(['startDate'])!.value,
      endDate: this.editForm.get(['endDate'])!.value,
      name: this.editForm.get(['name'])!.value,
      location: this.editForm.get(['location'])!.value,
      item: this.editForm.get(['item'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILetting>>): void {
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
