import React, { useContext, useEffect, useState } from "react";
import { AppContext } from "../providers/AppContext";
import { useNavigate } from "react-router-dom";
import { MainLayout } from "../layouts/MainLayout";
import { LoginInfo } from "../models/LoginInfo";
import { decryptData } from "../helpers/CryptoHelper";
import { getEventService } from "../api";
import { Loader } from "../components/Loader";

interface IAuthorizedRouteProps {
    children: React.ReactNode;
}

export const AuthorizedRoute: React.FC<IAuthorizedRouteProps> = ({ children }) => {
    const { loginInfo, setLoginInfo } = useContext(AppContext);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const navigate = useNavigate();

    useEffect(() => {
        setIsLoading(() => true);
        if (!LoginInfo.isAuthenticated(loginInfo)) {
            const encryptedPw = localStorage.getItem("encryptedCode");
            if (!encryptedPw || encryptedPw === "") {
                setIsLoading(() => false);
                navigate("/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? ""));
            }
            else {
                const decryptedCode = decryptData(encryptedPw);
                if (decryptedCode.split(":").length !== 2) {
                    setIsLoading(() => false);
                    navigate("/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? ""));
                }
                else {
                    const splitCode = decryptedCode.split(":");
                    const eventService = getEventService(splitCode[0], splitCode[1]);

                    eventService.getEvents()
                        .then(() => {
                            setLoginInfo(new LoginInfo(true, splitCode[0], splitCode[1]));
                            setIsLoading(() => false);
                        }).catch(() => {
                            console.log("Error while trying to login with stored credentials.")
                            setLoginInfo(new LoginInfo(false, "-", ""));
                            setIsLoading(() => false);
                            navigate("/login?returnUrl=" + encodeURIComponent(document.location.pathname ?? ""));
                        });
                }
            }
        }
        else {
            setIsLoading(() => false);
        }
    }, [loginInfo, setLoginInfo]);

    return (
        <MainLayout>
            {isLoading ? <div className="w-full flex items-center justify-center"><Loader /></div> : children}
        </MainLayout>
    );
};
