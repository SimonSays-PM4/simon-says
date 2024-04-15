import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FieldValues } from "react-hook-form";
import { EventCreateUpdateDTO } from "../../gen/api";
import { getEventService } from "../../api";
import { AppContext } from "../../providers/AppContext";

type EventActions = {
    deleteEvent: () => void;
    saveEvent: (eventToSave: FieldValues) => void;
    onFormInvalid: (eventToSave: FieldValues) => void;
};
type EventCreateReturnProps = {
    event: EventCreateUpdateDTO;
    errorMessage: string | undefined;
    eventActions: EventActions;
    isLoading: boolean;
    showDeleteModal: boolean;
    setShowDeleteModal: (thing: boolean) => void;
};
export const useEventCreatePage = (): EventCreateReturnProps => {
    const { id } = useParams();
    const eventId = id ? Number(id) : 0;

    const [event, setEvent] = useState<EventCreateUpdateDTO>({ id: 0, password: "", name: "", numberOfTables: 0 });
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    const navigate = useNavigate();

    const { loginInfo } = useContext(AppContext);
    const eventService = getEventService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (eventId > 0) {
            setIsLoading(true);
            eventService
                .getEvent(eventId)
                .then((response) => {
                    const receivedEvent = response.data as EventCreateUpdateDTO;
                    setEvent(receivedEvent);
                    setIsLoading(false);
                })
                .catch(() => {
                    // TODO: Add Error Handling
                    console.error("FAILED TO FETCH");
                    setIsLoading(false);
                });
        }
    }, [id]);

    const onFormInvalid = (data?: FieldValues) => {
        const eventToSave = data as EventCreateUpdateDTO;
        eventToSave.id = eventId > 0 ? eventId : 0;
        setEvent(eventToSave);
    };

    const saveEvent = useCallback(
        (data: FieldValues) => {
            setIsLoading(true);

            const eventToSave = data as EventCreateUpdateDTO;
            eventToSave.id = eventId > 0 ? eventId : undefined;

            eventService
                .putEvent(eventToSave)
                .then((response) => {
                    setIsLoading(false);
                    if (response.status === 201 || response.status === 200) {
                        navigate("../events");
                    } else {
                        setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
                    }
                })
                .catch(() => {
                    setIsLoading(false);
                    setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
                });
        },
        [event]
    );

    const deleteEvent = useCallback(() => {
        if (eventId > 0) {
            setIsLoading(true);
            eventService.deleteEvent(eventId).then(() => {
                setIsLoading(false);
                navigate("../events");
            });
        }
    }, [id]);

    const eventActions: EventActions = {
        saveEvent,
        deleteEvent,
        onFormInvalid
    };

    return { event, errorMessage, eventActions, isLoading, showDeleteModal, setShowDeleteModal };
};
