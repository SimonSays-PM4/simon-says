import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { EventCreateDTO } from "../../gen/api";
import { FormInput } from "../../components/form/FormInput";
import { nameof } from "ts-simple-nameof";
import {useEventCreatePage} from "./EventCreatePage.hooks.ts";

export const EventCreatePageComponent: React.FC = () => {
    const {event, errorMessage, saveEvent} = useEventCreatePage();
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm();

    return (
        <div>
            <h2 className="text-xl font-semibold text-default-800 mb-4">{event.id>0 ? <>Edit <b>"{event.name}"</b></>:"Event erstellen"}</h2>

            <form onSubmit={handleSubmit(saveEvent)}>
                <FormInput id={nameof<EventCreateDTO>(e => e.name)} defaultValue={event.name} label={"Name"} type="text" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.name)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.password)} label={"Passwort"} defaultValue={event.password} type="password" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.password)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.numberOfTables)} label={"Anzahl Tische"} defaultValue={String(event.numberOfTables)} type="number" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.numberOfTables)] ? fieldRequiredMessage : undefined} />

                {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}

                <Button buttonText="Erstellen" className="mt-4" type="submit" />
            </form>
        </div>
    );
}