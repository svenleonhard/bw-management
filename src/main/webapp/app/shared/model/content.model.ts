import { IItem } from 'app/shared/model/item.model';

export interface IContent {
  id?: number;
  description?: string;
  item?: IItem;
}

export class Content implements IContent {
  constructor(public id?: number, public description?: string, public item?: IItem) {}
}
