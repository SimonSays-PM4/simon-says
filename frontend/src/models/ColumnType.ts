export type ColumnType<Col> = {
    key: keyof Col;
    name: string;
    type?: string;
    action?: (col: Col) => void;
    formatter?: (col: Col) => string;
};
