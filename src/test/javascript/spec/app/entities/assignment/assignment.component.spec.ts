import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { BwManagementTestModule } from '../../../test.module';
import { AssignmentComponent } from 'app/entities/assignment/assignment.component';
import { AssignmentService } from 'app/entities/assignment/assignment.service';
import { Assignment } from 'app/shared/model/assignment.model';

describe('Component Tests', () => {
  describe('Assignment Management Component', () => {
    let comp: AssignmentComponent;
    let fixture: ComponentFixture<AssignmentComponent>;
    let service: AssignmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [BwManagementTestModule],
        declarations: [AssignmentComponent],
      })
        .overrideTemplate(AssignmentComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AssignmentComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AssignmentService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Assignment(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.assignments && comp.assignments[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
