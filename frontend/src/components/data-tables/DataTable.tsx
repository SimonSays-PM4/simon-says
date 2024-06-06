import { ColumnType } from "../../models/ColumnType"
import { Button } from "../Button"
import { ButtonType } from "../../enums/ButtonType.ts";
import { IoIosArrowBack, IoMdAdd } from "react-icons/io";
import { ReactElement } from "react";

type IDataTableTypeProps = {}

type IDataTableProps<T> = {
    rows: T[]
    columns: Array<ColumnType<T>>
    title: string,
    icon?: string | ReactElement | (string | ReactElement)[],
    onCreateClick: () => void,
    onBackClick?: () => void,
}

export const DataTable = <DataType extends IDataTableTypeProps>({ columns, rows, title, onCreateClick, onBackClick, icon }: IDataTableProps<DataType>) => {
    return (
        <div className="overflow-hidden rounded-lg border border-default-100">
            <div className="overflow-hidden p-6 ">
                <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
                    <div className="flex flex-wrap items-center gap-4">
                        {onBackClick ? <Button onClick={onBackClick} buttonType={ButtonType.Primary}><IoIosArrowBack /></Button> : <></>}
                        <div className="items-center gap-4">
                            <h2 className="text-xl font-semibold">{title}</h2>
                        </div>
                        <div className="flex flex-wrap">
                            <div className="text-xl font-semibold whitespace-nowrap">
                                {icon}
                            </div>
                        </div>
                    </div>
                    <div className="flex flex-wrap items-center gap-4">
                        <Button onClick={onCreateClick}>Erstellen <IoMdAdd /></Button>
                    </div>
                </div>
            </div>
            <div className="relative overflow-x-auto">
                <div className="inline-block min-w-full align-middle">
                    <div className="overflow-hidden">
                        <table className="min-w-full divide-y divide-default-200">
                            <thead className="bg-default-400/10">
                            <tr>
                                {columns.map((column) => {

                                    return column.type == "column" || column.type == "boolean" ? <th
                                        key={column.key as string}
                                        scope="col"
                                        className="whitespace-nowrap px-5 py-3 text-start text-xs font-medium uppercase text-default-500"
                                    >
                                        {column.name}
                                    </th>:<></>
                                })}
                                <th></th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-default-200">
                                {rows.length > 0
                                    ? rows.map((row, idx) => {
                                        return (
                                            <tr key={idx}>
                                                {columns.map((column, idx) => {
                                                    const tableData = row[column.key] as string;

                                                    if (column.formatter) {

                                                        let curr = "whitespace-nowrap px-5 py-3 text-sm"+(column.center?" text-center":"")+" text-default-800";
                                                        return (
                                                            <td key={idx} className={curr}>
                                                                {column.formatter(row)}
                                                            </td>
                                                        );
                                                    }
                                                    else {
                                                        return (column.type == "column" || column.type == "boolean" ?
                                                            <td key={idx} className="whitespace-nowrap px-5 py-3 text-sm text-default-800">
                                                                {column.type == "column" ? tableData : <input type="checkbox" checked={Boolean(tableData)} disabled />}
                                                            </td> : <></>
                                                        );
                                                    }
                                                })}

                                                <td className="flex place-content-end min-h-[60px] ml-auto">
                                                    {columns.filter((column) => column.type == "action" && column.action).map((column) => {
                                                        // @ts-ignore
                                                        return <Button id={column.elementKey} key={column.elementKey} className="my-2 mx-2" buttonType={column.buttonType} buttonText={column.noText ? "" : column.name} onClick={(() => column.action(row)) || console.log}>{column.children}</Button>
                                                    })}
                                                </td>
                                            </tr>
                                        );
                                    }) :
                                    (<div className="whitespace-nowrap px-5 py-3 text-center text-sm text-default-800">
                                        No Items
                                    </div>)}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};
