import { Moment } from 'moment';

export interface IImage {
  id?: number;
  dataContentType?: string;
  data?: any;
  uploadDate?: Moment;
}

export class Image implements IImage {
  constructor(public id?: number, public dataContentType?: string, public data?: any, public uploadDate?: Moment) {}
}
