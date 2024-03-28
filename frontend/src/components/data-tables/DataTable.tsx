import { useNavigate } from "react-router-dom"
import { ColumnType } from "../../models/ColumnType"
import { Button } from "../Button"

type IDataTableTypeProps = {}

type IDataTableProps<T> = {
    rows: T[]
    columns: Array<ColumnType<T>>
    title: string
}

export const DataTable = <DataType extends IDataTableTypeProps>({ columns, rows, title }: IDataTableProps<DataType>) => {
    const navigate = useNavigate();

    return (
        <div className="overflow-hidden rounded-lg border border-default-200">
            <div className="overflow-hidden p-6 ">
                <div className="flex flex-wrap items-center gap-4 sm:justify-between lg:flex-nowrap">
                    <h2 className="text-xl font-semibold text-default-800">{title}</h2>

                    <div className="flex flex-wrap items-center gap-4">
                        <Button buttonText="Create" />
                    </div>
                </div>
            </div>

            <div className="relative overflow-x-auto">
                <div className="inline-block min-w-full align-middle">
                    <div className="overflow-hidden">
                        <table className="min-w-full divide-y divide-default-200">
                            <thead className="bg-default-400/10">
                                <tr>
                                    {columns.map((column) => (
                                        <th
                                            key={column.key as string}
                                            scope="col"
                                            className="whitespace-nowrap px-5 py-3 text-start text-xs font-medium uppercase text-default-500"
                                        >
                                            {column.name}
                                        </th>
                                    ))}
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-default-200">
                                {rows.map((row, idx) => {
                                    return (
                                        <tr key={idx}>
                                            {columns.map((column, idx) => {
                                                const tableData = row[column.key] as string

                                                if (column.key == "name") {
                                                    return (
                                                        <td className="min-w-[190px] whitespace-nowrap px-5 py-3" key={tableData + idx}>
                                                            <div className="flex items-center gap-2">
                                                                <h4 className="text-sm font-medium text-default-800">{tableData}</h4>
                                                            </div>
                                                        </td>
                                                    )
                                                } else {
                                                    return (
                                                        <td key={idx} className="whitespace-nowrap px-5 py-3 text-sm text-default-800">
                                                            {tableData}
                                                        </td>
                                                    )
                                                }
                                            })}

                                            <td className="flex min-h-[60px]">
                                                <Button buttonText="Edit" className="my-2" onClick={() => navigate("/event/id123")} />
                                                <Button buttonText="Delete" className="my-2 ml-2" />
                                            </td>
                                        </tr>
                                    )
                                })}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};
