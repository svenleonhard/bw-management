import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { BwManagementTestModule } from '../../../test.module';
import { LettingComponent } from 'app/entities/letting/letting.component';
import { LettingService } from 'app/entities/letting/letting.service';
import { Letting } from 'app/shared/model/letting.model';

describe('Component Tests', () => {
  describe('Letting Management Component', () => {
    let comp: LettingComponent;
    let fixture: ComponentFixture<LettingComponent>;
    let service: LettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [BwManagementTestModule],
        declarations: [LettingComponent],
      })
        .overrideTemplate(LettingComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LettingComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LettingService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Letting(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.lettings && comp.lettings[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
