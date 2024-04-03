import {useCallback, useEffect, useState} from "react";
import {EventControllerApi, EventDTO} from "../../gen/api";

type EventActions = {
    deleteEvent:() => void,
    setEventIdToDelete:(id:number)=> void;
}
type EventListPageReturnProps = {
    eventActions:EventActions
    loading:boolean,
    showDeletePopup:boolean
    setShowDeletePopup:(show:boolean)=>void,
    data:EventDTO[]
}
export const useEventListPage = (): EventListPageReturnProps  => {
    const [eventIdToDelete, setEventIdToDelete] = useState(0)
    const [loading, setLoading] = useState(false)
    const [showDeletePopup, setShowDeletePopup] = useState(false)
    const [data, setData] = useState<EventDTO[]>([])

    const eventControllerApi = new EventControllerApi();

    useEffect( () => {
        if (!showDeletePopup) {
            reloadEvents();
        }
    }, [showDeletePopup]);

    const reloadEvents = useCallback(async () => {
        setLoading(true);
        eventControllerApi.getEvents().then((response) => {
                setData(response.data)
                setLoading(false)
            }
        )
    }, [])

    const deleteEvent = useCallback(() => {
        if (eventIdToDelete>0) {
            setLoading(true);
            eventControllerApi.deleteEvent(eventIdToDelete).then(()=>{
                setShowDeletePopup(false)
                setLoading(false);
            })
        }
    }, [eventIdToDelete])

    const eventActions:EventActions = {
        deleteEvent,
        setEventIdToDelete
    }

    return {eventActions,loading, showDeletePopup, setShowDeletePopup,data}
}