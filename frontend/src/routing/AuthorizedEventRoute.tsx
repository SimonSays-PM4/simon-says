import React from "react";
import { AuthorizedRoute } from "./AuthorizedRoute";
import { EventProvider } from "../providers/EventProvider";

interface IAuthorizedEventRouteProps {
    children: React.ReactNode;
}

export const AuthorizedEventRoute: React.FC<IAuthorizedEventRouteProps> = ({ children }) => {
    return <EventProvider><AuthorizedRoute children={children} /></EventProvider>;
};
