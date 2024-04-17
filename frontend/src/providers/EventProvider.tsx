import * as React from "react";
import { EventContext } from "./EventContext";
import { useParams } from "react-router-dom";

export interface IEventProviderProps {
    children: React.ReactNode;
}

const EventProvider: React.FC<IEventProviderProps> = ({ children }) => {
    const { eventId } = useParams();
    // TODO: what if no eventId is provided??

    return (
        <EventContext.Provider
            value={{
                eventId: eventId as unknown as number, // TODO: ugly code, fix this
            }}
        >
            <>{children}</>
        </EventContext.Provider>
    );
};

export { EventProvider };
