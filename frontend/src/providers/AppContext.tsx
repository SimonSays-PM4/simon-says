import * as React from "react";

export interface IAppContext {
}

const AppContext = React.createContext<IAppContext>({});

export { AppContext };
