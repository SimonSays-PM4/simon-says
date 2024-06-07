import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { FormInput } from "../../components/form/FormInput";
import { Popup } from "../../components/Popup.tsx";
import { useEventCreatePage } from "./EventCreatePage.hooks.ts";
import { EventCreateUpdateDTO } from "../../gen/api";
import { ButtonType } from "../../enums/ButtonType.ts";
import { useNavigate } from "react-router-dom";

export const EventCreatePageComponent: React.FC = () => {
    const { event, errorMessage, eventActions, setShowDeleteModal, showDeleteModal } = useEventCreatePage();
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
    const fieldLengthMessage = "Die Eingabe muss zwischen 5 und 64 Zeichen sein.";
    const navigate = useNavigate();
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
                    label={"Name"}
                    type="text"
                    register={register}
                    isRequired={true}
                    minLength={5}
                    defaultValue={event.name}
                    maxLength={64}
                    validationError={getErrorMessage(nameof<EventCreateUpdateDTO>(e => e.name))} />
                <FormInput id={nameof<EventCreateUpdateDTO>(e => e.password)}
                    label={"Passwort"}
                    type="password"
                    register={register}
                    isRequired={true}
                    defaultValue={event.password}
                    minLength={8}
                    maxLength={64}
                    validationError={getErrorMessage(nameof<EventCreateUpdateDTO>(e => e.password))} />
                <FormInput id={nameof<EventCreateUpdateDTO>(e => e.numberOfTables)}
                    label={"Anzahl Tische"}
                    type="number"
                    defaultValue={String(event.numberOfTables)}
                    register={register}
                    isRequired={true}
                    validationError={getErrorMessage(nameof<EventCreateUpdateDTO>(e => e.numberOfTables))} />

                {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}
                <div className="flex min-h-[60px] items-end ml-auto">
                    <Button buttonText={event.id != undefined && event.id > 0 ? "Speichern" : "Erstellen"} className="my-2" type="submit" />
                    {event.id != undefined && event.id > 0 && <Button buttonText="Löschen" className="my-2 mx-2" onClick={() => setShowDeleteModal(true)} />}
                    <Button buttonText="Abbrechen" className="my-2 ml-2" onClick={() => navigate("../events")} buttonType={ButtonType.Secondary} />
                </div>
            </form>

            <Popup modalText="Möchten Sie diesen Event löschen?" show={showDeleteModal} onClose={() => setShowDeleteModal(false)} onAccept={eventActions.deleteEvent} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}