import { useNavigate, useParams } from "react-router-dom";

import { FieldValues, useForm } from "react-hook-form";
import React, { useContext } from "react";
import { Button } from "../../components/Button";
import { FormInput } from "../../components/form/FormInput";
import { getEventService } from "../../api.ts";
import { AppContext } from "../../providers/AppContext.tsx";
import { LoginInfo } from "../../models/LoginInfo.ts";
import { NotificationType } from "../../enums/NotificationType.ts";

export const JoinPage: React.FC = () => {
    const { eventId } = useParams();
    const navigate = useNavigate();
    const { setLoginInfo, addNotification } = useContext(AppContext);

    const {
        register,
        handleSubmit,
    } = useForm();

    const onSubmit = async (data: FieldValues) => {
        const eventService = getEventService(data["userName"], data["password"]);
        eventService.getEvent(Number(eventId)).then(() => {
            setLoginInfo(new LoginInfo(true, data["userName"], data["password"]));
            navigate(`/${eventId}`);
        }).catch(() => {
            addNotification(NotificationType.ERR, "Invalid username or password");
        });
    }

    return (
        <main
            role="main"
            className="w-full px-[20px] py-[24px] md:px-[46px] md:py-[48px] bg-white"
        >
            <div className="w-full flex items-center justify-center h-[90%] min-h-[450px]">
                <div className="w-[500px] h-fit grid justify-center items-center">
                    <h1 className="font-bold flex items-center justify-center text-[42px] sm:text-[46px] xl:text-[52px] mb-8 leading-snug">
                        Join Event
                    </h1>
                    <div className="grid h-fit mt-6">
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <FormInput id="userName" label={"Username"} register={register} type="text" />
                            <FormInput id="password" label={"Password"} register={register} type="text" />
                            <Button
                                buttonText="Join"
                                type="submit"
                                className="mt-6"
                            />
                        </form>
                    </div>
                </div>
            </div>
        </main>
    );
};
