import { IContent } from 'app/shared/model/content.model';
import { ILetting } from 'app/shared/model/letting.model';

export interface IItem {
  id?: number;
  qrCode?: number;
  description?: string;
  picture?: string;
  contents?: IContent[];
  lettings?: ILetting[];
}

export class Item implements IItem {
  constructor(
    public id?: number,
    public qrCode?: number,
    public description?: string,
    public picture?: string,
    public contents?: IContent[],
    public lettings?: ILetting[]
  ) {}
}
