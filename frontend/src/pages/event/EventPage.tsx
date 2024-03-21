import { FieldValues, useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { TextInput } from "../../components/form/TextInput";

export const EventPage: React.FC = () => {
    const {
        register,
        handleSubmit,
    } = useForm();

    const onSubmit = (data: FieldValues) => console.log(data);

    return (
        <div>
            <h1 className="mb-4">Event xy</h1>

            <form onSubmit={handleSubmit(onSubmit)}>
                <TextInput id={"name"} label={"Name"} register={register} />

                <Button buttonText="Save" className="mt-4" type="submit" />
            </form>
        </div>
    );
}