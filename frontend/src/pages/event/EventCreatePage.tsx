import { FieldValues, useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { EventControllerApi, EventCreateDTO } from "../../gen/api";
import { FormInput } from "../../components/form/FormInput";
import { nameof } from "ts-simple-nameof";
import { useNavigate } from "react-router-dom";

export const EventCreatePage: React.FC = () => {
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
    const eventControllerApi = new EventControllerApi();
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm();

    const onSubmit = async (data: FieldValues) => {
        const eventCreateDto = data as EventCreateDTO;
        const response = await eventControllerApi.createEvent(eventCreateDto);
        console.log(response);
        // TODO: Handle response

        navigate("/events");
    };

    return (
        <div>
            <h2 className="text-xl font-semibold text-default-800 mb-4">Event erstellen</h2>

            <form onSubmit={handleSubmit(onSubmit)}>
                <FormInput id={nameof<EventCreateDTO>(e => e.name)} label={"Name"} type="text" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.name)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.password)} label={"Passwort"} type="password" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.password)] ? fieldRequiredMessage : undefined} />
                <FormInput id={nameof<EventCreateDTO>(e => e.numberOfTables)} label={"Anzahl Tische"} type="number" register={register} isRequired={true} validationError={errors && errors[nameof<EventCreateDTO>(e => e.numberOfTables)] ? fieldRequiredMessage : undefined} />

                <Button buttonText="Erstellen" className="mt-4" type="submit" />
            </form>
        </div>
    );
}