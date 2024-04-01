import { useNavigate } from "react-router-dom";
import { Button } from "../components/Button";
import { FormInput } from "../components/form/FormInput";
import { FieldValues, useForm } from "react-hook-form";
import { AppContext } from "../providers/AppContext";
import React from "react";
import { LoginInfo } from "../models/LoginInfo";

export const LoginPage: React.FC = () => {
    const { setLoginInfo } = React.useContext(AppContext);
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
    } = useForm();

    const onSubmit = (data: FieldValues) => {
        console.log(data);
        setLoginInfo(new LoginInfo(true, "Test-User"));
        navigate("/");
    }

    return (
        <main
            role="main"
            className="w-full px-[20px] py-[24px] md:px-[46px] md:py-[48px] bg-white"
        >
            <div className="w-full flex items-center justify-center h-[90%] min-h-[450px]">
                <div className="w-[500px] h-fit grid justify-center items-center">
                    <h1 className="font-bold flex items-center justify-center text-[42px] sm:text-[46px] xl:text-[52px] mb-8 leading-snug">
                        Login
                    </h1>
                    <div className="grid h-fit mt-6">
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <FormInput id={"Code"} label={"Code"} register={register} type="text" />

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