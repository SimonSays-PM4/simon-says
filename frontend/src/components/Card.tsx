import React, { ReactElement } from "react";

type CardProps = {
    onClick: () => void, 
    title: string,
    iconType: ReactElement
}

export const Card: React.FC<CardProps> = ({ onClick, title, iconType }) => {
    return (
        <div onClick={onClick} className="w-64 p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 mb-4 md:mb-4 md:w-64 mx-4" style={{ cursor: 'pointer' }}>
            <div style={{ fontSize: "42px"}}>{iconType}</div>
            <h5 className="mb-2 text-lg font-bold tracking-tight text-gray-900 ">{title}</h5>
        </div>
    );
}
