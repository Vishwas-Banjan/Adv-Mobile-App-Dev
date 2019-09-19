import { AuthService } from '../auth.service';
import { JwtPayload } from '../interfaces/jwt-payload.interface';
declare const JwtStrategy_base: new (...args: any[]) => any;
export declare class JwtStrategy extends JwtStrategy_base {
    private authService;
    constructor(authService: AuthService);
    validate(payload: JwtPayload): Promise<{
        expiresIn: number;
        token: string;
    }>;
}
export {};
