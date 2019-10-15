import * as mongoose from 'mongoose';

export const FilterSchema = new mongoose.Schema({
  minor: String,
  major: String,
  region: String,
  filter: String,
  uuid: String,
});
