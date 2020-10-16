import { Moment } from 'moment';
import { IItem } from 'app/shared/model/item.model';

export interface ILetting {
  id?: number;
  startDate?: Moment;
  endDate?: Moment;
  name?: string;
  location?: string;
  item?: IItem;
}

export class Letting implements ILetting {
  constructor(
    public id?: number,
    public startDate?: Moment,
    public endDate?: Moment,
    public name?: string,
    public location?: string,
    public item?: IItem
  ) {}
}
