import React, {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {AppContext} from "../providers/AppContext.tsx";

export const HomePage: React.FC = () => {
    const { loginInfo } = React.useContext(AppContext);
    const navigate = useNavigate();

    useEffect(()=>{
        navigate("/admin/events")
    },[loginInfo])
    return (
        <div>
            <div className="text-2xl">Welcome to the <b>Simon Says</b>!</div>
            <br/>
            <h1>Home Page</h1>
        </div>
    );
}