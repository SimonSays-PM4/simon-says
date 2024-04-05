import * as React from "react";
import { NavLink } from "react-router-dom";

export interface INavigationItemProps {
    text: string;
    link: string
    isActive: boolean;
}

export const NavigationItem: React.FC<INavigationItemProps> = ({ text, link, isActive }) => {
    return (
        <NavLink
            className={
                "aspect-[1/1] justify-center group w-[75px] md:w-[80px] p-1 md:p-2 rounded-[0.65rem] flex flex-col text-sm font-medium sm:mx-[5px] md:mx-0 " +
                (isActive
                    ? "transform-gpu bg-primary drop-shadow-[0_3px_6px_rgba(124,7,49,0.35)] md:drop-shadow-[0_5px_15px_rgba(124,7,49,0.35)] text-white stroke-primaryfont"
                    : "bg-default-200 hover:bg-secondary text-secondaryfont stroke-secondaryfont")
            }
            to={link}
        >
            <div className="text-center overflow-hidden">
                <p className="sm:inline overflow-hidden text-ellipsis">{text}</p>
            </div>
        </NavLink>
    );
};
