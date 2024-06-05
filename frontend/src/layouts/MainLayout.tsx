import * as React from "react";
import { Header } from "../components/Header";
import {Footer} from "../components/Footer.tsx";

export interface IMainLayoutProps {
    children: React.ReactNode;
}

export const MainLayout: React.FC<IMainLayoutProps> = ({ children }) => {
    return (
        <div>
            <div className="h-full w-screen flex flex-col md:flex-row">
                <div role="main" className="flex-1 flex flex-col overflow-y-auto items-center h-full">
                    <div className="w-full h-full">
                        <Header />
                        <div className="flex-1 flex items-stretch px-5 pt-5">
                            {children}
                            <Footer/>
                        </div>
                        <div>
                            <div className="bottom-0 left-0">
                                <div className="px-10 pt-10 text-center lg:text-center"><></></div>
                                <div className="px-10 pt-5 text-center lg:text-center"></div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};
