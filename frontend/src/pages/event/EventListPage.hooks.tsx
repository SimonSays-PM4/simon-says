import { useCallback, useEffect, useState } from "react";
import {eventService} from "../../api.ts";
import {EventDTO} from "../../../gen/api";

type EventActions = {
    deleteEvent: () => void,
    setEventToDelete: (id: EventDTO) => void,
    eventToDelete: EventDTO
}
type EventListPageReturnProps = {
    eventActions: EventActions
    loading: boolean,
    showDeletePopup: boolean
    setShowDeletePopup: (show: boolean) => void,
    data: EventDTO[]
}
export const useEventListPage = (): EventListPageReturnProps => {
    const [eventToDelete, setEventToDelete] = useState<EventDTO>({ id: 0, name: "", numberOfTables: 0, password: "" })
    const [loading, setLoading] = useState(false)
    const [showDeletePopup, setShowDeletePopup] = useState(false)
    const [data, setData] = useState<EventDTO[]>([])

    useEffect(() => {
        if (!showDeletePopup) {
            reloadEvents();
        }
    }, [showDeletePopup]);

    const reloadEvents = useCallback(() => {
        setLoading(true);
        eventService.getEvents().then((response) => {
            setData(response.data)
            setLoading(false)
        }
        )
    }, [])

    const deleteEvent = useCallback(() => {
        if (eventToDelete.id && eventToDelete.id > 0) {
            setLoading(true);
            eventService.deleteEvent(eventToDelete.id).then(() => {
                setShowDeletePopup(false)
                setLoading(false);
            })
        }
    }, [eventToDelete.id])

    const eventActions: EventActions = {
        deleteEvent,
        setEventToDelete,
        eventToDelete
    }

    return { eventActions, loading, showDeletePopup, setShowDeletePopup, data }
}