import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AppContext } from "../providers/AppContext.tsx";

export const HomePage: React.FC = () => {
    const { loginInfo } = React.useContext(AppContext);
    const navigate = useNavigate();

    useEffect(() => {
        navigate("/admin/events")
    }, [loginInfo]);

    return (
        <div>
            <div className="text-2xl">Willkommen bei <b>Simon Says</b>!</div>
            <br />
            <h1>Startseite</h1>
        </div>
    );
}