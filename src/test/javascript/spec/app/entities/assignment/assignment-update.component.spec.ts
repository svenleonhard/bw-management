import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { BwManagementTestModule } from '../../../test.module';
import { AssignmentUpdateComponent } from 'app/entities/assignment/assignment-update.component';
import { AssignmentService } from 'app/entities/assignment/assignment.service';
import { Assignment } from 'app/shared/model/assignment.model';

describe('Component Tests', () => {
  describe('Assignment Management Update Component', () => {
    let comp: AssignmentUpdateComponent;
    let fixture: ComponentFixture<AssignmentUpdateComponent>;
    let service: AssignmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [BwManagementTestModule],
        declarations: [AssignmentUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(AssignmentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AssignmentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AssignmentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Assignment(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Assignment();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
