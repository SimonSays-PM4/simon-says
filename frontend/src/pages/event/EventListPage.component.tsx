import React from "react";
import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { Loader } from "../../components/Loader";
import {EventCreateDTO, EventDTO} from "../../gen/api";
import { useNavigate } from "react-router-dom";
import {useEventListPage} from "./EventListPage.hooks.tsx";
import {Popup} from "../../components/Popup.tsx";

export const EventListPageComponent: React.FC = () => {
    const {loading, eventActions, showDeletePopup, setShowDeletePopup,data} = useEventListPage()
    const navigate = useNavigate();

    const columns: Array<ColumnType<EventDTO>> = [
        {
            key: "name",
            name: "Name",
        },
        {
            key: "numberOfTables",
            name: "Anzahl Tische",
        }
    ];

    const onEditClick = (row: EventCreateDTO) => {
        navigate("../event/create/"+row.id)
    }

    const onDeleteClick = (row: EventCreateDTO) => {
        eventActions.setEventIdToDelete(row.id);
        setShowDeletePopup(true);
    }

    return (
        <div className="w-full">
            {loading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<EventDTO> title="Events" columns={columns} rows={data} onCreateClick={() => navigate("/event/create")} onEditClick={onEditClick} onDeleteClick={onDeleteClick} />
            )}
            <Popup show={showDeletePopup} onClose={()=> setShowDeletePopup(false)} onAccept={eventActions.deleteEvent} modalText="Delete this Event?"/>
        </div>
    );
}