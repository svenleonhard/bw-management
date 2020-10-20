import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ILetting } from 'app/shared/model/letting.model';

type EntityResponseType = HttpResponse<ILetting>;
type EntityArrayResponseType = HttpResponse<ILetting[]>;

@Injectable({ providedIn: 'root' })
export class LettingService {
  public resourceUrl = SERVER_API_URL + 'api/lettings';

  constructor(protected http: HttpClient) {}

  create(letting: ILetting): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(letting);
    return this.http
      .post<ILetting>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(letting: ILetting): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(letting);
    return this.http
      .put<ILetting>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILetting>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILetting[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(letting: ILetting): ILetting {
    const copy: ILetting = Object.assign({}, letting, {
      startDate: letting.startDate && letting.startDate.isValid() ? letting.startDate.format(DATE_FORMAT) : undefined,
      endDate: letting.endDate && letting.endDate.isValid() ? letting.endDate.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate ? moment(res.body.startDate) : undefined;
      res.body.endDate = res.body.endDate ? moment(res.body.endDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((letting: ILetting) => {
        letting.startDate = letting.startDate ? moment(letting.startDate) : undefined;
        letting.endDate = letting.endDate ? moment(letting.endDate) : undefined;
      });
    }
    return res;
  }
}
