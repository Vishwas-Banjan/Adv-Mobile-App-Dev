import * as bcrypt from 'bcrypt';
import * as mongoose from 'mongoose';
import { User } from 'src/types/user';

export const UserSchema = new mongoose.Schema({
  password: {
    type: String,
    select: false,
  },
  created: { type: Date, default: Date.now },
  firstName: String,
  lastName: String,
  email: String,
  city: String,
  gender: String,
});

UserSchema.pre('save', async function(next: mongoose.HookNextFunction) {
  const user = this as User;
  try {
    if (!this.isModified('password')) {
      return next();
    }
    const hashed = await bcrypt.hash(user.password, 10);
    user.password = hashed;
    return next();
  } catch (err) {
    return next(err);
  }
});
