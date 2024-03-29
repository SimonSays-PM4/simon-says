import React from "react";
import { useNavigate } from "react-router-dom"; import { FieldValues, useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { EventControllerApi, EventCreateDTO } from "../../gen/api";
import { FormInput } from "../../components/form/FormInput";
import { nameof } from "ts-simple-nameof";

export const EventCreatePage: React.FC = () => {
    const [errorMessage, setErrorMessage] = React.useState<string | undefined>(undefined);

    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
    const eventControllerApi = new EventControllerApi();
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm();

    const onSubmit = async (data: FieldValues) => {
        try {
            const eventCreateDto = data as EventCreateDTO;
            const response = await eventControllerApi.createEvent(eventCreateDto);

            if (response.status === 201) {
                navigate("/events");
            }
            else {
                setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
            }
        } catch (_) {
            setErrorMessage("Beim Erstellen des Events ist ein Fehler aufgetreten.");
        }
    };

    return (
        <div>
            <h2 className="text-xl font-semibold text-default-800 mb-4">Event erstellen</h2>


            <form onSubmit={handleSubmit(onSubmit)}>
                <FormInput id={nameof<EventCreateDTO>(e => e.name)} label={"Name"} type="text" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.name)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.password)} label={"Passwort"} type="password" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.password)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.numberOfTables)} label={"Anzahl Tische"} type="number" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.numberOfTables)] ? fieldRequiredMessage : undefined} />

                {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}

                <Button buttonText="Erstellen" className="mt-4" type="submit" />
            </form>
        </div>
    );
}