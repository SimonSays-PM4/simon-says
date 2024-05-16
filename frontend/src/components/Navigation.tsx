import * as React from "react";
import { NavigationItem } from "./NavigationItem";
import { ActivePageType } from "../enums/ActivePageType";
import {AppContext} from "../providers/AppContext.tsx";
import {EventContext} from "../providers/EventContext.tsx";

export interface INavigationProps {
    activePageType: ActivePageType;
}

export const Navigation: React.FC<INavigationProps> = ({ activePageType }) => {
    const { loginInfo } = React.useContext(AppContext);
    const { eventId} = React.useContext(EventContext);

    return (
        <nav className="w-full md:w-[100px] bg-white overflow-y-auto block md:border-r md:border-[rgba(0,0,0,0.15)]">
            <div className="flex justify-center md:justify-center mb-4 md:mb-0 md:absolute md:top-1/2 md:translate-y-[-50%] w-full md:w-[100px] mt-4 md:mt-0">
                <div className="flex flex-row justify-around md:max-w-none md:flex-col md:flex-1 mt-2 mx-[10px] md:mx-0 md:mt-6 w-full md:px-2 md:space-y-3">
                    {
                        loginInfo.userName=="admin"?<><NavigationItem
                            text="Home"
                            link="/"
                            isActive={activePageType === ActivePageType.Home}
                        /><NavigationItem
                            text="Events"
                            link="/admin/events"
                            isActive={activePageType === ActivePageType.EventList}
                        /></>:<><NavigationItem
                            text="Stations"
                            link={"/"+eventId}
                            isActive={activePageType === ActivePageType.Home}
                        /></>
                    }

                </div>
            </div>
        </nav>
    );
};
