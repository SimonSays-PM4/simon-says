import * as React from "react";
import { AppContext } from "./AppContext";

export interface IAppProviderProvideProps {
    children: React.ReactNode;
}

const AppProvider: React.FC<IAppProviderProvideProps> = ({ children }: IAppProviderProvideProps) => {
    return (
        <AppContext.Provider
            value={{}}
        >
            <>{children}</>
        </AppContext.Provider>
    );
};

export { AppProvider };
