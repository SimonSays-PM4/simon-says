import * as React from "react";
import {AppContext} from "./AppContext";
import {LoginInfo} from "../models/LoginInfo";
import {Bounce, toast, ToastContainer} from 'react-toastify';
import {NotificationType} from "../enums/NotificationType.ts";

export interface IAppProviderProvideProps {
    children: React.ReactNode;
}

const AppProvider: React.FC<IAppProviderProvideProps> = ({ children }: IAppProviderProvideProps) => {
    const [loginInfo, setLoginInfo] = React.useState<LoginInfo>(new LoginInfo(false, "-"));
    const addNotification = (type:NotificationType, message:string) => {
        if (type == NotificationType.OK) {
            toast.success(message, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "light",
                transition: Bounce,
            });
        } else if (type == NotificationType.ERR) {
            toast.error(message, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "light",
                transition: Bounce,
            });
        } else if (type == NotificationType.WARN) {
            toast.warn(message, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "light",
                transition: Bounce,
            });
        } else {
            toast(message, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "light",
                transition: Bounce,
            });
        }
    }
    return (
        <AppContext.Provider
            value={{
                loginInfo,
                setLoginInfo,
                addNotification
            }}
        ><ToastContainer></ToastContainer>
            <>{children}</>
        </AppContext.Provider>
    );
};

export { AppProvider };
