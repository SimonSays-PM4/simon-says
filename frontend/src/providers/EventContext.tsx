import * as React from "react";

export interface IEventContext {
    eventId: number;
}

const EventContext = React.createContext<IEventContext>({eventId:0});

export { EventContext };
