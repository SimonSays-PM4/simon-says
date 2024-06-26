import * as React from "react";
import { classNames } from "../helpers/ClassNameHelper";
import { ButtonType } from "../enums/ButtonType";
import {ReactElement} from "react";

export interface IButtonProps {
    id?:string
    buttonText?: string;
    icon?: React.SVGProps<SVGSVGElement>;
    reactIcon?: React.FC
    type?: "submit" | "button" | "reset" | undefined;
    disabled?: boolean;
    className?: string;
    buttonType?: ButtonType;
    onClick?: React.MouseEventHandler<HTMLButtonElement>;
    children?:  ReactElement | (string | ReactElement)[]
}

export const Button: React.FC<IButtonProps> = ({
    id,
    buttonText,
    icon,
    disabled = false,
    type = "button",
    buttonType = ButtonType.Primary,
    className,
    onClick,
    children
}) => {
    return (
        <button id={id}
            className={classNames("w-fit min-h-[36px] flex items-center gap-[4px] px-[12px] text-[16px] leading-[22px] tracking-[0.4px] font-bold rounded-[6px]",
                "hover:shadow-md hover:shadow-shadow disabled:hover:shadow-none disabled:bg-disabled disabled:text-disabledfont stroke-primaryfont disabled:stroke-disabledfont focus:shadow-lg focus:border-[#B7B7B7] border border-primary disabled:border-disabled",
                className ?? "",
                disabled ? "bg-slate-100 cursor-not-allowed border-slate-100" : "",
                buttonType === ButtonType.Secondary ? "bg-white text-[#990000]" : "bg-primary text-white hover:bg-hoverprimary")}
            type={type}
            onClick={onClick}
            disabled={disabled}
        >
            <>
                {icon}
                {buttonText}
                {children}
            </>
        </button>
    );
};
