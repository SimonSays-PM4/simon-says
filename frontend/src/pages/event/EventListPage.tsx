import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { EventType } from "../../models/Event";

export const EventListPage: React.FC = () => {
    const orderRows = [
        { date: "2024-06-06", name: "Event 1" },
        { date: "2024-10-10", name: "Event 2" },
    ];

    const columns: Array<ColumnType<EventType>> = [
        {
            key: "date",
            name: "Datum",
        },
        {
            key: "name",
            name: "Name",
        }
    ];

    return (
        <div>
            <DataTable<EventType> title="Events" columns={columns} rows={orderRows} />
        </div>
    );
}