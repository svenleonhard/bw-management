import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IAssignment, Assignment } from 'app/shared/model/assignment.model';
import { AssignmentService } from './assignment.service';
import { IItem } from 'app/shared/model/item.model';
import { ItemService } from 'app/entities/item/item.service';

@Component({
  selector: 'jhi-assignment-update',
  templateUrl: './assignment-update.component.html',
})
export class AssignmentUpdateComponent implements OnInit {
  isSaving = false;
  boxitems: IItem[] = [];
  boxes: IItem[] = [];

  editForm = this.fb.group({
    id: [],
    description: [],
    comment: [],
    boxItem: [],
    box: [],
  });

  constructor(
    protected assignmentService: AssignmentService,
    protected itemService: ItemService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ assignment }) => {
      this.updateForm(assignment);

      this.itemService
        .query({ 'assignmentId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IItem[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IItem[]) => {
          if (!assignment.boxItem || !assignment.boxItem.id) {
            this.boxitems = resBody;
          } else {
            this.itemService
              .find(assignment.boxItem.id)
              .pipe(
                map((subRes: HttpResponse<IItem>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IItem[]) => (this.boxitems = concatRes));
          }
        });

      this.itemService
        .query({ 'assignmentId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IItem[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IItem[]) => {
          if (!assignment.box || !assignment.box.id) {
            this.boxes = resBody;
          } else {
            this.itemService
              .find(assignment.box.id)
              .pipe(
                map((subRes: HttpResponse<IItem>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IItem[]) => (this.boxes = concatRes));
          }
        });
    });
  }

  updateForm(assignment: IAssignment): void {
    this.editForm.patchValue({
      id: assignment.id,
      description: assignment.description,
      comment: assignment.comment,
      boxItem: assignment.boxItem,
      box: assignment.box,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const assignment = this.createFromForm();
    if (assignment.id !== undefined) {
      this.subscribeToSaveResponse(this.assignmentService.update(assignment));
    } else {
      this.subscribeToSaveResponse(this.assignmentService.create(assignment));
    }
  }

  private createFromForm(): IAssignment {
    return {
      ...new Assignment(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      boxItem: this.editForm.get(['boxItem'])!.value,
      box: this.editForm.get(['box'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAssignment>>): void {
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
