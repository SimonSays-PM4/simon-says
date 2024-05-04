import * as React from "react";
import { LoginInfo } from "../models/LoginInfo";
import { NotificationType } from "../enums/NotificationType.ts";
import 'react-toastify/dist/ReactToastify.css';

export interface IAppContext {
    loginInfo: LoginInfo;
    setLoginInfo: React.Dispatch<React.SetStateAction<LoginInfo>>;
    addNotification: (type: NotificationType, message: string) => void;
}

const AppContext = React.createContext<IAppContext>(undefined!);

export { AppContext };
