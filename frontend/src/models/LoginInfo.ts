export class LoginInfo {
    isAuthenticated: boolean;
    userName: string;
    password: string;

    constructor(isAuthenticated: boolean, userName: string, password: string) {
        this.isAuthenticated = isAuthenticated;
        this.userName = userName;
        this.password = password;
    }

    static isAuthenticated(loginInfo: LoginInfo) {
        return loginInfo?.isAuthenticated ?? false;
    }
}
