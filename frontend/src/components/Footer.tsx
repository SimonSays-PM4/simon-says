import React from "react";
import {ServerIndicator} from "./ServerIndicator.tsx";

export const Footer: React.FC = () => {


    return (
        <footer>
            <div style={{position: 'absolute', bottom:'0', right:'0'}} className="text-center lg:text-right sticky bottom-0">
                <div className="bg-white/5 p-3 text-right text-surface">
                    <ServerIndicator/>
                </div>
            </div>
        </footer>

    );
};
