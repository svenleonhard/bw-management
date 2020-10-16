import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IAssignment } from 'app/shared/model/assignment.model';

type EntityResponseType = HttpResponse<IAssignment>;
type EntityArrayResponseType = HttpResponse<IAssignment[]>;

@Injectable({ providedIn: 'root' })
export class AssignmentService {
  public resourceUrl = SERVER_API_URL + 'api/assignments';

  constructor(protected http: HttpClient) {}

  create(assignment: IAssignment): Observable<EntityResponseType> {
    return this.http.post<IAssignment>(this.resourceUrl, assignment, { observe: 'response' });
  }

  update(assignment: IAssignment): Observable<EntityResponseType> {
    return this.http.put<IAssignment>(this.resourceUrl, assignment, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAssignment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAssignment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
