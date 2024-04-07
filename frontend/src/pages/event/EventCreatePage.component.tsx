import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { FormInput } from "../../components/form/FormInput";
import { nameof } from "ts-simple-nameof";
import { Popup } from "../../components/Popup.tsx";
import { useEventCreatePage } from "./EventCreatePage.hooks.ts";
import {EventCreateUpdateDTO} from "../../../gen/api";

export const EventCreatePageComponent: React.FC = () => {
    const { event, errorMessage, eventActions, setShowDeleteModal, showDeleteModal } = useEventCreatePage();
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
    const fieldLengthMessage = "Die Eingabe ist muss zwischen 5 und 64 Zeichen sein.";

    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues
    } = useForm();

    const getErrorMessage = (fieldId: string) => {
        if (errors && errors[fieldId] !== undefined) {
            if (errors[fieldId]!.type === "required") {
                return fieldRequiredMessage;
            }
            if (errors[fieldId]!.type === "minLength" || errors[fieldId]!.type === "maxLength") {
                return fieldLengthMessage;
            }

            return undefined;
        }
    };

    return (
        <div>
            <h2 className="text-xl font-semibold text-default-800 mb-4">{event.id && event.id > 0 ? <>Edit <b>"{event.name}"</b></> : "Event erstellen"}</h2>

            <form onSubmit={handleSubmit(() => eventActions.saveEvent(getValues()), () => eventActions.onFormInvalid(getValues()))}>
                <FormInput id={nameof<EventCreateUpdateDTO>(e => e.name)}
                    defaultValue={event.name}
                    label={"Name"}
                    type="text"
                    register={register}
                    isRequired={true}
                    minLength={5}
                    maxLength={64}
                    validationError={getErrorMessage(nameof<EventCreateUpdateDTO>(e => e.name))} />
                <FormInput id={nameof<EventCreateUpdateDTO>(e => e.password)}
                    label={"Passwort"}
                    defaultValue={event.password}
                    type="password"
                    register={register}
                    isRequired={true}
                    minLength={8}
                    maxLength={64}
                    validationError={getErrorMessage(nameof<EventCreateUpdateDTO>(e => e.password))} />
                <FormInput id={nameof<EventCreateUpdateDTO>(e => e.numberOfTables)}
                    label={"Anzahl Tische"}
                    defaultValue={String(event.numberOfTables)}
                    type="number" register={register}
                    isRequired={true}
                    validationError={getErrorMessage(nameof<EventCreateUpdateDTO>(e => e.numberOfTables))} />

                {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}
                <div className="flex min-h-[60px] items-end ml-auto">
                    <Button buttonText={event.id != undefined && event.id > 0 ? "Speichern" : "Erstellen"} className="my-2" type="submit" />
                    {event.id != undefined && event.id > 0 && <Button buttonText="Delete" className="my-2 mx-2" onClick={() => setShowDeleteModal(true)} />}
                </div>
            </form>

            <Popup modalText="Do you want to delete this event?" show={showDeleteModal} onClose={() => setShowDeleteModal(false)} onAccept={eventActions.deleteEvent} />
        </div>
    );
}