import { IItem } from 'app/shared/model/item.model';

export interface IAssignment {
  id?: number;
  description?: string;
  comment?: string;
  boxItem?: IItem;
  box?: IItem;
}

export class Assignment implements IAssignment {
  constructor(public id?: number, public description?: string, public comment?: string, public boxItem?: IItem, public box?: IItem) {}
}
