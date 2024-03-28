import * as React from "react";
import { Navigation } from "../components/Navigation";
import { ActivePageType } from "../enums/ActivePageType";
import { Header } from "../components/Header";

export interface IMainLayoutProps {
    children: React.ReactNode;
    activePageType: ActivePageType;
}

export const MainLayout: React.FC<IMainLayoutProps> = ({ children, activePageType }) => {
    return (
        <div className="h-full w-screen flex flex-col md:flex-row">
            <Navigation activePageType={activePageType} />

            <div role="main" className="flex-1 flex flex-col overflow-y-auto items-center h-full">
                <div className="w-full h-full">
                    <Header />

                    <div className="flex-1 flex items-stretch px-[10px] sm:px-[40px] pb:[42px] pt-[20px] md:pt-[42px] h-[92%] mx-2 md:mx-0">{children}</div>
                </div>
            </div>
        </div>
    );
};
