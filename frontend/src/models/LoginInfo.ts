import { UserRole } from "../enums/UserRole";

export class LoginInfo {
    isAuthenticated: boolean;
    userName: string;
    password: string;
    role: UserRole;

    constructor(isAuthenticated: boolean, userName: string, password: string, role: UserRole) {
        this.isAuthenticated = isAuthenticated;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    static isAuthenticated(loginInfo: LoginInfo) {
        if (!loginInfo) {
            return false;
        }

        return loginInfo.isAuthenticated;
    }
}
