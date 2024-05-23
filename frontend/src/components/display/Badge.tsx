import * as React from "react";
import { ReactElement } from "react";

type BadgeProps = {
    children: ReactElement | string | (string | ReactElement)[];
    color: string;
    classNames?: string;
}

export const Badge: React.FC<BadgeProps> = ({ color, children, classNames }) => {
    return (
        <span className={(classNames ?? "") + " inline-block whitespace-nowrap rounded-full bg-success-100 px-[0.65em] pb-[0.5em] pt-[0.35em] text-center align-baseline font-bold text-white leading-none text-success-700 bg-" + color + "-500"}>
            {children}
        </span>
    );
};