import { useNavigate } from "react-router-dom";
import { Button } from "../components/Button";
import { TextInput } from "../components/form/TextInput";
import { FieldValues, useForm } from "react-hook-form";

export const LoginPage: React.FC = () => {
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
    } = useForm();

    const onSubmit = (data: FieldValues) => console.log(data);

    return (
        <main
            role="main"
            className="w-full px-[20px] py-[24px] md:px-[46px] md:py-[48px] bg-white"
        >
            <div className="w-full flex items-center justify-center h-[90%] min-h-[450px]">
                <div className="w-[500px] h-fit grid justify-center items-center">
                    <h1 className="font-bold flex items-center justify-center text-[42px] sm:text-[46px] xl:text-[52px] mb-8 leading-snug">
                        Login
                    </h1>
                    <div className="grid h-fit mt-6">
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <TextInput id={"Code"} label={"Code"} register={register} />

                            <Button
                                buttonText="Login"
                                type="submit"
                                className="mt-6"
                                onClick={() => { navigate("/") }}
                            />
                        </form>
                    </div>
                </div>
            </div>
        </main>
    );
};
