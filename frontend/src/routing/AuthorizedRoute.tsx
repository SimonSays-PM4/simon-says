import React from "react";
import { AppContext } from "../providers/AppContext";
import { ActivePageType } from "../enums/ActivePageType";
import { Navigate } from "react-router-dom";
import { MainLayout } from "../layouts/MainLayout";
import { LoginInfo } from "../models/LoginInfo";

interface IAuthorizedRouteProps {
    activePageType: ActivePageType;
    children: React.ReactNode;
}

export const AuthorizedRoute: React.FC<IAuthorizedRouteProps> = ({ children, activePageType }) => {
    const { loginInfo } = React.useContext(AppContext);

    if (LoginInfo.isAuthenticated(loginInfo) === false) {
        return (
            <Navigate to={"/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? "")} />
        );
    }

    return <MainLayout activePageType={activePageType}>{children}</MainLayout>
};
