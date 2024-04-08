import React from "react";
import {ServerIndicator} from "./ServerIndicator.tsx";

export const Footer: React.FC = () => {


    return (
        <footer className="text-center lg:text-left sticky bottom-0">
            <div className="bg-white/5 p-3 text-right text-surface">
                <ServerIndicator/>
            </div>
        </footer>
    );
};
