import * as React from "react";

export enum StatusLevel {
    OK,
    ERROR,
    WARNING
}

type StatusProps = {
    text:string,
    status:StatusLevel,
    tooltip:string,
    textNext:string
}

export const Status: React.FC<StatusProps> = ({text,status,textNext,tooltip}) => {
    let color = "inline-block whitespace-nowrap rounded-full bg-success-100 px-[0.65em] pb-[0.5em] pt-[0.35em] text-center align-baseline font-bold text-white leading-none text-success-700 bg-green-500"
    if (status==StatusLevel.ERROR) {
        color = "inline-block whitespace-nowrap rounded-full bg-success-100 px-[0.65em] pb-[0.5em] pt-[0.35em] text-center align-baseline font-bold text-white leading-none text-success-700 bg-red-500"
    } else if(status==StatusLevel.WARNING) {
        color = "inline-block whitespace-nowrap rounded-full bg-success-100 px-[0.65em] pb-[0.5em] pt-[0.35em] text-center align-baseline font-bold text-white leading-none text-success-700 bg-yellow-500"
    }


    return (<><div className="text-[0.75em]"><span
        title={tooltip}
        className={color}>{text} - {textNext} - {APP_VERSION}

        </span></div></>

    );
};