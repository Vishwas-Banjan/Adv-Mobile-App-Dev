export interface CreateUserDTO {
  firstName: string;
  lastName: string;
  email: string;
  city: string;
  gender: string;
}

export type UpdateUserDTO = Partial<CreateUserDTO>;
