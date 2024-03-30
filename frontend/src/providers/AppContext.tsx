import * as React from "react";
import { LoginInfo } from "../models/LoginInfo";

export interface IAppContext {
    loginInfo: LoginInfo;
    setLoginInfo: React.Dispatch<React.SetStateAction<LoginInfo>>;
}

const AppContext = React.createContext<IAppContext>(undefined!);

export { AppContext };
