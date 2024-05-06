import React from "react";
import { AppContext } from "../providers/AppContext";
import { ActivePageType } from "../enums/ActivePageType";
import { Navigate } from "react-router-dom";
import { MainLayout } from "../layouts/MainLayout";
import { LoginInfo } from "../models/LoginInfo";
import { decryptData } from "../helpers/CryptoHelper";
import { getEventService } from "../api";

interface IAuthorizedRouteProps {
    activePageType: ActivePageType;
    children: React.ReactNode;
}

export const AuthorizedRoute: React.FC<IAuthorizedRouteProps> = ({ children, activePageType }) => {
    const { loginInfo, setLoginInfo } = React.useContext(AppContext);

    if (LoginInfo.isAuthenticated(loginInfo) === false) {
        const encryptedPw = localStorage.getItem("encryptedCode");
        console.log(encryptedPw);
        if (!encryptedPw) {
            <Navigate to={"/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? "")} />
        }
        else {
            const decryptedCode = decryptData(encryptedPw);
            console.log(decryptedCode);
            if (decryptedCode.split(":").length !== 2) {
                <Navigate to={"/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? "")} />
            }
            else {
                const splitCode = decryptedCode.split(":");
                console.log(splitCode);

                const eventService = getEventService(splitCode[0], splitCode[1]);
                eventService.getEvents().then(() => {
                    setLoginInfo(new LoginInfo(true, splitCode[0], splitCode[1]));
                }).catch(() => {
                    console.log("Invalid password");
                });
                setLoginInfo(() => new LoginInfo(true, splitCode[0], splitCode[1]));
            }
        }
    }

    return <MainLayout activePageType={activePageType}>{children}</MainLayout>
};
