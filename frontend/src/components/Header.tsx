import React from "react";
import { AppContext } from "../providers/AppContext";
import { useNavigate } from "react-router-dom";
import { LoginInfo } from "../models/LoginInfo";
import { Button } from "./Button";

export const Header: React.FC = () => {
    const { setLoginInfo, loginInfo } = React.useContext(AppContext);
    const navigate = useNavigate();

    const invokeLogout = () => {
        localStorage.removeItem("encryptedCode");
        setLoginInfo(new LoginInfo(false, "-", ""));
        navigate("/login");
    };

    return (
        <div className="w-full pt-[16px] border-b border-default-200 h-[80px]">
            <div className="md:bg-white md:h-full md:w-[45%] lg:w-[33.33%] w-full flex md:top-[-150px] md:left-10 top-[25px] right-[25px] sm:left-[65%] md:static">
                <p className="ml-10 my-auto">{loginInfo.userName}</p>
                <Button
                    className="ml-10 my-auto"
                    onClick={invokeLogout}
                    type="button"
                    buttonText="Logout"
                />
            </div>
        </div>
    );
};
