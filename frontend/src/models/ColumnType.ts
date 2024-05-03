import {ButtonType} from "../enums/ButtonType.ts";
import {ReactElement} from "react";

export type ColumnType<Col> = {
    key: keyof Col;
    elementKey?: string;
    name: string;
    type?: string;
    buttonType?: ButtonType;
    children?:ReactElement | (string | ReactElement)[];
    action?: (col: Col) => void;
    noText?:boolean;
    center?:boolean;
    formatter?: (col: Col) => string | ReactElement | (string | ReactElement)[];
};
