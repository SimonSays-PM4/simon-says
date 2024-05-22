import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/Button";
import { FormInput } from "../components/form/FormInput";
import { FieldValues, useForm } from "react-hook-form";
import { AppContext } from "../providers/AppContext";
import { LoginInfo } from "../models/LoginInfo";
import { encryptData } from "../helpers/CryptoHelper";
import { getEventService } from "../api";
import { NotificationType } from "../enums/NotificationType";

export const LoginPage: React.FC = () => {
    const { setLoginInfo, addNotification } = useContext(AppContext);
    const navigate = useNavigate();
    const adminUser = "admin";

    const {
        register,
        handleSubmit,
    } = useForm();

    const isLocalUrl = (url: string): boolean => {
        // A local URL should start with '/' and not contain '://'
        return url.startsWith('/') && !url.includes('://');
    }

    const onSubmit = (data: FieldValues) => {
        const enteredCode = data["code"];
        const eventService = getEventService(adminUser, enteredCode);

        eventService.getEvents().then(() => {
            const encryptedPw = encryptData(adminUser + ":" + enteredCode);
            localStorage.setItem("encryptedCode", encryptedPw);
            setLoginInfo(new LoginInfo(true, adminUser, data["code"]));

            const searchParams = new URLSearchParams(location.search);
            const returnUrl = searchParams.get('returnUrl');
            if (returnUrl && isLocalUrl(returnUrl)) {
                navigate(returnUrl);
            }
            else {
                navigate("/");
            }
        }).catch(() => {
            addNotification(NotificationType.ERR, "Invalider Code");
        });
    }

    return (
        <main role="main" className="relative w-full px-[20px] py-[24px] md:px-[46px] md:py-[48px] bg-white">
            <div className="w-full flex items-center justify-center h-[90%] min-h-[450px]">
                <div className="w-[500px] h-fit grid justify-center items-center">
                    <h1 className="font-bold flex items-center justify-center text-[42px] sm:text-[46px] xl:text-[52px] mb-8 leading-snug">
                        Login
                    </h1>
                    <div className="grid h-fit mt-6">
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <FormInput id="code" label="Code" register={register} type="text" />
                            <Button
                                buttonText="Login"
                                type="submit"
                                className="mt-6"
                            />
                        </form>
                    </div>
                </div>
            </div>
        </main>
    );
};
