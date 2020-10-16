import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { BwManagementTestModule } from '../../../test.module';
import { LettingUpdateComponent } from 'app/entities/letting/letting-update.component';
import { LettingService } from 'app/entities/letting/letting.service';
import { Letting } from 'app/shared/model/letting.model';

describe('Component Tests', () => {
  describe('Letting Management Update Component', () => {
    let comp: LettingUpdateComponent;
    let fixture: ComponentFixture<LettingUpdateComponent>;
    let service: LettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [BwManagementTestModule],
        declarations: [LettingUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(LettingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LettingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LettingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Letting(123);
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
        const entity = new Letting();
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
