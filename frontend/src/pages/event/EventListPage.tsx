import React from "react";
import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { Loader } from "../../components/Loader";
import { EventCreateDTO } from "../../gen/api";
import { useNavigate } from "react-router-dom";

export const EventListPage: React.FC = () => {
    const [isLoading, setLoading] = React.useState<boolean>(true);
    const navigate = useNavigate();

    const orderRows = [
        { name: "Event 1", numberOfTables: 5 } as EventCreateDTO,
        { name: "Event 2", numberOfTables: 12 } as EventCreateDTO,
    ];

    const columns: Array<ColumnType<EventCreateDTO>> = [
        {
            key: "name",
            name: "Name",
        },
        {
            key: "numberOfTables",
            name: "Anzahl Tische",
        }
    ];

    const loadEventsAsync = React.useCallback(async () => {
        setLoading(true);

        try {
            // TODO: Load events over api

            // wait for 1 second to simulate loading
            await new Promise((resolve) => setTimeout(resolve, 1000));
        } catch (error) {
            // TODO: Handle error
            console.error(error);
        }

        setLoading(false);
    }, []);

    const onEditClick = (row: EventCreateDTO) => {
        // TODO: Implement edit click
        console.log(row);
    }

    const onDeleteClick = (row: EventCreateDTO) => {
        // TODO: Implement delete click
        console.log(row);
    }

    React.useEffect(() => {
        loadEventsAsync();
    }, [loadEventsAsync]);

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<EventCreateDTO> title="Events" columns={columns} rows={orderRows} onCreateClick={() => navigate("/event/create")} onEditClick={onEditClick} onDeleteClick={onDeleteClick} />
            )}
        </div>
    );
}