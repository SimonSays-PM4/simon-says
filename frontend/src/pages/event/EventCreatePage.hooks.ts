import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FieldValues } from "react-hook-form";
import { getEventService } from "../../api.ts";
import {EventCreateUpdateDTO} from "../../gen/api";
import { AppContext } from "../../providers/AppContext.tsx";
import { NotificationType } from "../../enums/NotificationType.ts";
import { EventContext } from "../../providers/EventContext.tsx";

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
    const { eventId } = useContext(EventContext);

    const [event, setEvent] = useState<EventCreateUpdateDTO>({ id: 0, password: "", name: "", numberOfTables: 0 });
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [showDeleteModal, setShowDeleteModal] = useState<boolean>(false);
    const navigate = useNavigate();

    const appContext = useContext(AppContext);
    const eventService = getEventService(appContext.loginInfo.userName, appContext.loginInfo.password);

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
                    appContext.addNotification(NotificationType.ERR, `Failed to fetch event with id ${eventId}`);
                    setIsLoading(false);
                });
        }
    }, [eventId]);

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
                        appContext.addNotification(NotificationType.OK, `Successfully saved the event`);
                    } else {
                        appContext.addNotification(
                            NotificationType.ERR,
                            `Beim Erstellen des Events ist ein Fehler aufgetreten.`
                        );
                        setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
                    }
                })
                .catch(() => {
                    setIsLoading(false);
                    appContext.addNotification(
                        NotificationType.ERR,
                        `Beim Erstellen des Events ist ein Fehler aufgetreten.`
                    );
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
    }, [eventId]);

    const eventActions: EventActions = {
        saveEvent,
        deleteEvent,
        onFormInvalid
    };

    return { event, errorMessage, eventActions, isLoading, showDeleteModal, setShowDeleteModal };
};
