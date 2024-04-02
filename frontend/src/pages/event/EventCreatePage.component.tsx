import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { EventCreateDTO } from "../../gen/api";
import { FormInput } from "../../components/form/FormInput";
import { nameof } from "ts-simple-nameof";
import {useEventCreatePage} from "./EventCreatePage.hooks.ts";
import {Popup} from "../../components/Popup.tsx";

export const EventCreatePageComponent: React.FC = () => {
    const {event, errorMessage, eventActions, setShowDeleteModal, showDeleteModal} = useEventCreatePage();
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm();

    return (
        <div>
            <h2 className="text-xl font-semibold text-default-800 mb-4">{event.id>0 ? <>Edit <b>"{event.name}"</b></>:"Event erstellen"}</h2>

            <form onSubmit={handleSubmit(eventActions.saveEvent)}>
                <FormInput id={nameof<EventCreateDTO>(e => e.name)} defaultValue={event.name} label={"Name"} type="text" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.name)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.password)} label={"Passwort"} defaultValue={event.password} type="password" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.password)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.numberOfTables)} label={"Anzahl Tische"} defaultValue={String(event.numberOfTables)} type="number" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.numberOfTables)] ? fieldRequiredMessage : undefined} />

                {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}
                <div className="flex min-h-[60px] items-end ml-auto">
                    <Button buttonText="Erstellen" className="my-2" type="submit" />
                    {event.id > 0 &&<Button buttonText="Delete" className="my-2 mx-2" onClick={() => setShowDeleteModal(true)}/>}
                </div>
            </form>
            <Popup modalText="Do you want to delete this event?" show={showDeleteModal} onClose={()=> setShowDeleteModal(false)} onAccept={eventActions.deleteEvent}/>
        </div>
    );
}