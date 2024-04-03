import {EventControllerApi, EventCreateDTO} from "../../gen/api";
import {useCallback, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {FieldValues} from "react-hook-form";

type EventCreateReturnProps = {
    event:EventCreateDTO,
    errorMessage: string | undefined,
    saveEvent:(eventToSave:FieldValues)=>void,
    isLoading:boolean;
}
export const useEventCreatePage = (): EventCreateReturnProps  => {

    const {id} = useParams();
    const eventId = id?Number(id):0;

    const [event, setEvent] = useState<EventCreateDTO>({id:0, password:"", name:"",numberOfTables:0})
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const eventControllerApi = new EventControllerApi();
    const navigate = useNavigate();

    useEffect(()=> {
        if (eventId>0) {
            // TODO: API call to get event
            const fetchedEvent : EventCreateDTO = {id:3, name:"Test Edit Event", password:"TEST", numberOfTables:34}
            setEvent(fetchedEvent)
        }

    },[id])

    const saveEvent = useCallback((data:FieldValues) => {
        const eventToSave = data as EventCreateDTO;
        setIsLoading(true);

        eventControllerApi.createEvent(eventToSave).then((response) => {
            setIsLoading(false);
            if (response.status === 201) {
                navigate("/events");
            } else {
                setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
            }
        }).catch(() => {
            setIsLoading(false);
            setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
        })
    }, [event]);

    return {event, errorMessage, saveEvent, isLoading}
}