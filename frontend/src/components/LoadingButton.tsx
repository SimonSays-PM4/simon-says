import * as React from "react";
import { ButtonType } from "../enums/ButtonType";
import { Button, IButtonProps } from "./Button";
import { Spinner } from "./Spinner";

export interface ILoadingButtonProps extends IButtonProps {
    isLoading: boolean;
}

export const LoadingButton: React.FC<ILoadingButtonProps> = ({
    buttonText,
    icon,
    disabled = false,
    type = "button",
    buttonType = ButtonType.Primary,
    className,
    isLoading,
    onClick,
}) => {
    return (
        <Button icon={isLoading ? <Spinner /> : (icon ?? <></>)} buttonText={buttonText} buttonType={buttonType} disabled={disabled} type={type} className={className} onClick={onClick} />
    );
};
