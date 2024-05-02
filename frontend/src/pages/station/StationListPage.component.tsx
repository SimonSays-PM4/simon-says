import React from "react";
import {useStationListPage} from "./StationListPage.hooks.ts";
import {Loader} from "../../components/Loader.tsx";
import {DataTable} from "../../components/data-tables/DataTable.tsx";
import {Popup} from "../../components/Popup.tsx";
import {useNavigate} from "react-router-dom";
import {StationDTO} from "../../gen/api";
import {ColumnType} from "../../models/ColumnType.ts";

export const StationListPageComponent: React.FC = () => {
    const {isLoading,stationList,stationActions,showDeletePopup,setShowDeletePopup} = useStationListPage();
    const navigate = useNavigate();

    const onEditClick = (row: StationDTO) => {
        navigate(`../station/create/` + row.id);
    };

    const onDeleteClick = (row: StationDTO) => {
        stationActions.setStationToDelete(row);
        setShowDeletePopup(true);
    };

    const columns: Array<ColumnType<StationDTO>> = [
        {
            key: "name",
            name: "Name",
            type: "column"
        },
        {
            key: "assemblyStation",
            name: "is Assembly Station",
            type: "boolean"
        },
        {
            key: "id",
            name: "Bearbeiten",
            type: "action",
            action: onEditClick
        },
        {
            key: "id",
            name: "Löschen",
            type: "action",
            action: onDeleteClick
        }
    ];

    return <div className="w-full">
        {isLoading ? (
            <div className="w-[100px] block mx-auto"><Loader /></div>
        ) : (
            <DataTable<StationDTO> title="Stations" columns={columns} rows={stationList} onCreateClick={() => navigate(`../station/create`)} />
        )}

        <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={stationActions.deleteStation} modalText={'Station "' + stationActions.stationToDelete.name + '" löschen?'} closeText="Abbrechen" acceptText="Löschen" />
    </div>
}