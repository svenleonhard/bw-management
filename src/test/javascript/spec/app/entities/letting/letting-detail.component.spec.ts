import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BwManagementTestModule } from '../../../test.module';
import { LettingDetailComponent } from 'app/entities/letting/letting-detail.component';
import { Letting } from 'app/shared/model/letting.model';

describe('Component Tests', () => {
  describe('Letting Management Detail Component', () => {
    let comp: LettingDetailComponent;
    let fixture: ComponentFixture<LettingDetailComponent>;
    const route = ({ data: of({ letting: new Letting(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [BwManagementTestModule],
        declarations: [LettingDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(LettingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LettingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load letting on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.letting).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
