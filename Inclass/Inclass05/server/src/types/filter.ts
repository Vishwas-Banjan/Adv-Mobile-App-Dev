import { Document } from 'mongoose';

export interface Filter extends Document {
  minor: string;
  major: string;
  region: string;
  filter: string;
  uuid: string;
}
