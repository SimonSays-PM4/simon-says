import * as React from "react";
import { AppContext } from "./AppContext";
import { LoginInfo } from "../models/LoginInfo";

export interface IAppProviderProvideProps {
    children: React.ReactNode;
}

const AppProvider: React.FC<IAppProviderProvideProps> = ({ children }: IAppProviderProvideProps) => {
    const [loginInfo, setLoginInfo] = React.useState<LoginInfo>(new LoginInfo(false, "-", ""));

    return (
        <AppContext.Provider
            value={{
                loginInfo,
                setLoginInfo,
            }}
        >
            <>{children}</>
        </AppContext.Provider>
    );
};

export { AppProvider };
