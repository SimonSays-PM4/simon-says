import React, { useEffect } from "react";
import { AppContext } from "../providers/AppContext";
import { useNavigate } from "react-router-dom";
import { MainLayout } from "../layouts/MainLayout";
import { LoginInfo } from "../models/LoginInfo";
import { decryptData } from "../helpers/CryptoHelper";
import { getEventService } from "../api";

interface IAuthorizedRouteProps {
    children: React.ReactNode;
}

export const AuthorizedRoute: React.FC<IAuthorizedRouteProps> = ({ children }) => {
    const { loginInfo, setLoginInfo } = React.useContext(AppContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (!LoginInfo.isAuthenticated(loginInfo)) {
            const encryptedPw = localStorage.getItem("encryptedCode");
            if (!encryptedPw || encryptedPw === "") {
                navigate("/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? ""))
            }
            else {
                const decryptedCode = decryptData(encryptedPw);
                if (decryptedCode.split(":").length !== 2) {
                    navigate("/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? ""))
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

    return <MainLayout>{children}</MainLayout>
};
