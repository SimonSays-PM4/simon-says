import * as React from "react";

import "./Loader.css";

export const Loader: React.FC = () => {
    return (
        <div className="fancy-spinner my-auto items-center">
            <div className="ring"></div>
            <div className="ring"></div>
            <div className="dot"></div>
        </div>
    );
};
