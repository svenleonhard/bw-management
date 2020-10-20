import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILetting } from 'app/shared/model/letting.model';

@Component({
  selector: 'jhi-letting-detail',
  templateUrl: './letting-detail.component.html',
})
export class LettingDetailComponent implements OnInit {
  letting: ILetting | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ letting }) => (this.letting = letting));
  }

  previousState(): void {
    window.history.back();
  }
}
