import {useCallback, useEffect, useState} from "react";
import {EventCreateDTO} from "../../gen/api";
type EventActions = {
    deleteEvent:() => void,
    setEventIdToDelete:(id:number)=> void;
}
type EventListPageReturnProps = {
    eventActions:EventActions
    loading:boolean,
    showDeletePopup:boolean
    setShowDeletePopup:(show:boolean)=>void,
    data:EventCreateDTO[]
}
export const useEventListPage = (): EventListPageReturnProps  => {
    const [eventIdToDelete, setEventIdToDelete] = useState(0)
    const [loading, setLoading] = useState(false)
    const [showDeletePopup, setShowDeletePopup] = useState(false)
    const [data, setData] = useState<EventCreateDTO[]>([])

    useEffect( () => {
        if (!showDeletePopup) {
            reloadEvents();
        }
    }, [showDeletePopup]);

    const reloadEvents = useCallback(async () => {
        setLoading(true);
        // TODO: Load events over api
        // wait for 1 second to simulate loading
        await new Promise((resolve) => setTimeout(resolve, 1000));
        setData([
            { id:45,name: "Event 1", numberOfTables: 5 } as EventCreateDTO,
            { id:34,name: "Event 2", numberOfTables: 12 } as EventCreateDTO,
        ]);
        setLoading(false)
    }, [])

    const deleteEvent = useCallback(() => {
        if (eventIdToDelete>0) {
            // TODO: API call to delete event
            setLoading(true);
            setEventIdToDelete(0)
            setShowDeletePopup(false)
            setLoading(false);
        }
    }, [eventIdToDelete])

    const eventActions:EventActions = {
        deleteEvent,
        setEventIdToDelete
    }

    return {eventActions,loading, showDeletePopup, setShowDeletePopup,data}
}