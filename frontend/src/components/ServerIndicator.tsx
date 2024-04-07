import * as React from "react";
import {useEffect, useState} from "react";
import {Status, StatusLevel} from "./Status.tsx";
import {API_URL, eventService} from "../api.ts";

type ServerIndicatorProps = {

}

export const ServerIndicator: React.FC<ServerIndicatorProps> = () => {

    const [status,setStatus] = useState(StatusLevel.WARNING)
    const [message,setMessage] = useState("Unknown")

    const currentStage = import.meta.env.MODE == "development" ? "DEV" : import.meta.env.MODE == "staging"? "STAGING": "PROD";

    useEffect(() => {
        eventService.getEvents().then(()=>{
            setStatus(StatusLevel.OK);
            setMessage("Server Up")
        }).catch((error)=>{
            console.log(error)
            setStatus(StatusLevel.ERROR);
            setMessage("Failed")
        })
    })



    return (<Status tooltip={API_URL} text={message} status={status} textNext={currentStage}/>
    );
};