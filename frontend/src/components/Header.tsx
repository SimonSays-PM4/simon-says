import React from "react";
import { AppContext } from "../providers/AppContext";
import { NavLink, useNavigate } from "react-router-dom";
import { LoginInfo } from "../models/LoginInfo";
import { Button } from "./Button";
import { MdFoodBank } from "react-icons/md";
import { EventContext } from "../providers/EventContext.tsx";

export const Header: React.FC = () => {
    const { setLoginInfo, loginInfo } = React.useContext(AppContext);
    const { eventId } = React.useContext(EventContext);
    const navigate = useNavigate();

    const invokeLogout = () => {
        localStorage.removeItem("encryptedCode");
        setLoginInfo(new LoginInfo(false, "-", ""));
        if (loginInfo.isAuthenticated) {
            if (loginInfo.userName!="admin" && eventId>0) {
                navigate("/"+eventId+"/join");
            } else {
                navigate("/login");
            }
        }
    };

    return (
        <div className="w-full p-3 border-b border-default-200  justify-between">
            <div className="flex flex-wrap gap-3 justify-between">
                <div className=" content-center text-2xl font-bold">
                    <div className="flex gap-4 items-center">
                        <NavLink to={loginInfo.userName != "admin" ? "/" + eventId : "/"} className="flex-shrink-0 flex items-center">
                            <div className="border shadow rounded-md p-1 max-w-sm w-30 mx-auto">
                                <div><MdFoodBank fontSize="40" /></div>
                            </div>
                        </NavLink>
                        <div>Simon Says</div>
                    </div>
                </div>
                <div className="p-4 flex gap-4">
                    <p className="ml-10 my-auto">{loginInfo.userName}</p>
                    <Button
                        className="ml-5 my-auto"
                        onClick={invokeLogout}
                        type="button"
                        buttonText="Logout"
                    />
                </div>
            </div>
        </div>
    );
};
