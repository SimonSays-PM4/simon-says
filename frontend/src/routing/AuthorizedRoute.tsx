import React, { useEffect } from "react";
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

    useEffect(() => {
        if (LoginInfo.isAuthenticated(loginInfo) === false) {
            const encryptedPw = localStorage.getItem("encryptedCode");
            if (!encryptedPw || encryptedPw === "") {
                <Navigate to={"/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? "")} />
            }
            else {
                const decryptedCode = decryptData(encryptedPw);
                if (decryptedCode.split(":").length !== 2) {
                    <Navigate to={"/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? "")} />
                }
                else {
                    const splitCode = decryptedCode.split(":");
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
    }, [loginInfo, setLoginInfo]);

    return <MainLayout activePageType={activePageType}>{children}</MainLayout>
};
