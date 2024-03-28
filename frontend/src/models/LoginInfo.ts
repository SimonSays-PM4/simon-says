export class LoginInfo {
    isAuthenticated: boolean;
    userName: string;

    constructor(isAuthenticated: boolean, userName: string) {
        this.isAuthenticated = isAuthenticated;
        this.userName = userName;
    }

    static isAuthenticated(loginInfo: LoginInfo) {
        if (!loginInfo) {
            return false;
        }

        return loginInfo.isAuthenticated;
    }
}
