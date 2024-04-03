import {EventControllerApi, EventPutDTO} from "../../gen/api";
import {useCallback, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {FieldValues} from "react-hook-form";


type EventActions = {
    deleteEvent: () => void,
    saveEvent: (eventToSave:FieldValues) => void;
}
type EventCreateReturnProps = {
    event:EventPutDTO,
    errorMessage: string | undefined,
    eventActions:EventActions
    isLoading:boolean
    showDeleteModal:boolean,
    setShowDeleteModal:(thing:boolean)=>void;
}
export const useEventCreatePage = (): EventCreateReturnProps  => {

    const {id} = useParams();
    const eventId = id?Number(id):0;

    const [event, setEvent] = useState<EventPutDTO>({id:0, password:"", name:"",numberOfTables:0})
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [showDeleteModal,setShowDeleteModal] = useState(false)

    const eventControllerApi = new EventControllerApi();
    const navigate = useNavigate();

    useEffect(()=> {
        if (eventId>0) {
            setIsLoading(true)
            eventControllerApi.getEvent(eventId).then((response)=> {
                const receivedEvent = response.data as EventPutDTO
                receivedEvent.password = "";
                setEvent(receivedEvent);
                setIsLoading(false);
            }).catch(() => {
                // TODO: Add Error Handling
                console.error("FAILED TO FETCH");
                setIsLoading(false);
            })
        }

    },[id])

    const saveEvent = useCallback((data:FieldValues) => {
        const eventToSave = data as EventPutDTO;

        setIsLoading(true);
        if (eventId>0) {
            eventToSave.id = eventId;
            eventControllerApi.putEvent(eventToSave).then((response) => {
                setIsLoading(false);
                if (response.status === 201 || response.status === 200) {
                    navigate("/events");
                } else {
                    setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
                }
            }).catch(() => {
                setIsLoading(false);
                setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
            })
        } else {
            eventControllerApi.createEvent(eventToSave).then((response) => {
                setIsLoading(false);
                if (response.status === 201 || response.status === 200) {
                    navigate("/events");
                } else {
                    setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
                }
            }).catch(() => {
                setIsLoading(false);
                setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
            })
        }
    }, [event]);

    const deleteEvent = useCallback(() => {
        if (eventId>0) {
            setIsLoading(true);
            eventControllerApi.deleteEvent(eventId).then(()=>{
                setIsLoading(false);
                navigate("/events");
            })
        }
    }, [id])

    const eventActions: EventActions = {
        saveEvent,
        deleteEvent
    }

    return {event, errorMessage, eventActions, isLoading,showDeleteModal, setShowDeleteModal}
}