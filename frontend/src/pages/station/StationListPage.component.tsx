import React from "react";
import { useStationListPage } from "./StationListPage.hooks.ts";
import { Loader } from "../../components/Loader.tsx";
import { DataTable } from "../../components/data-tables/DataTable.tsx";
import { Popup } from "../../components/Popup.tsx";
import { useNavigate } from "react-router-dom";
import { StationDTO } from "../../gen/api";
import { ColumnType } from "../../models/ColumnType.ts";
import { MdEditSquare } from "react-icons/md";
import { FaRegTrashAlt } from "react-icons/fa";
import { PiChargingStationDuotone } from "react-icons/pi";
import { ButtonType } from "../../enums/ButtonType.ts";

export const StationListPageComponent: React.FC = () => {
    const { isLoading, stationList, stationActions, showDeletePopup, setShowDeletePopup, eventId } = useStationListPage();
    const navigate = useNavigate();

    const onEditClick = (row: StationDTO) => {
        navigate(`../station/create/` + row.id);
    };

    const onDeleteClick = (row: StationDTO) => {
        stationActions.setStationToDelete(row);
        setShowDeletePopup(true);
    };

    const onViewStation = (row: StationDTO) => {
        navigate(`../../../` + eventId + `/station/` + row.id)
    };

    const columns: Array<ColumnType<StationDTO>> = [
        {
            key: "name",
            name: "Name",
            type: "column"
        },
        {
            key: "assemblyStation",
            name: "Ist Zusammensetzungs-Station",
            type: "boolean"
        },
        {
            key: "id",
            elementKey: "viewAction",
            type: "action",
            name: "Anzeigen",
            buttonType: ButtonType.Secondary,
            action: onViewStation
        },
        {
            key: "id",
            name: "Bearbeiten",
            elementKey: "editAction",
            type: "action",
            children: <MdEditSquare />,
            noText: true,
            action: onEditClick
        },
        {
            key: "id",
            name: "Löschen",
            elementKey: "deleteAction",
            type: "action",
            children: <FaRegTrashAlt />,
            noText: true,
            action: onDeleteClick
        },
    ];

    return <div className="w-full">
        {isLoading ? (
            <div className="w-[100px] block mx-auto"><Loader /></div>
        ) : (
            <DataTable<StationDTO> title="Stationen" icon={<PiChargingStationDuotone />} columns={columns} rows={stationList} onCreateClick={() => navigate(`../station/create`)} onBackClick={() => navigate("../../events")} />
        )}

        <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={stationActions.deleteStation} modalText={'Möchten Sie die Station "' + stationActions.stationToDelete.name + '" löschen?'} closeText="Abbrechen" acceptText="Löschen" />
    </div>
}