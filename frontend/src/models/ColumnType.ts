export type ColumnType<Col> = {
    key: keyof Col;
    name: string;
    type?:string;
    action?:(thing:Col)=>void;
};
