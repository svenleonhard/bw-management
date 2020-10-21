import { IImage } from 'app/shared/model/image.model';
import { IContent } from 'app/shared/model/content.model';
import { ILetting } from 'app/shared/model/letting.model';

export interface IItem {
  id?: number;
  qrCode?: number;
  description?: string;
  picture?: IImage;
  contents?: IContent[];
  lettings?: ILetting[];
  parent?: IItem;
}

export class Item implements IItem {
  constructor(
    public id?: number,
    public qrCode?: number,
    public description?: string,
    public picture?: IImage,
    public contents?: IContent[],
    public lettings?: ILetting[],
    public parent?: IItem
  ) {}
}
