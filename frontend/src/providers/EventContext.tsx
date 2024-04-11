import * as React from "react";

export interface IEventContext {
    eventId: number;
}

const EventContext = React.createContext<IEventContext>(undefined!);

export { EventContext };
