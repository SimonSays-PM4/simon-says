import React from "react";
import { ActivePageType } from "../enums/ActivePageType";
import { AuthorizedRoute } from "./AuthorizedRoute";
import { EventProvider } from "../providers/EventProvider";

interface IAuthorizedEventRouteProps {
    activePageType: ActivePageType;
    children: React.ReactNode;
}

export const AuthorizedEventRoute: React.FC<IAuthorizedEventRouteProps> = ({ children, activePageType }) => {
    return <EventProvider><AuthorizedRoute activePageType={activePageType} children={children} /></EventProvider>;
};
