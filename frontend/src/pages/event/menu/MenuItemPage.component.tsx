import React from "react";
import { useMenuItemPage } from "./MenuItemPage.hooks.tsx";
import { Loader } from "../../../components/Loader.tsx";
import { ColumnType } from "../../../models/ColumnType.ts";
import { MenuItemDTO } from "../../../gen/api";
import { DataTable } from "../../../components/data-tables/DataTable.tsx";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../../components/Popup.tsx";

export const MenuItemPage: React.FC = () => {
    const { event, menuItems, isLoading, menuItemActions, showDeletePopup, setShowDeletePopup } = useMenuItemPage();
    const navigate = useNavigate();

    if (isLoading) {
        return <Loader />
    }

    const onEditClick = (row: MenuItemDTO) => {
        navigate("./create/" + row.id)
    }

    const onDeleteClick = (row: MenuItemDTO) => {
        if (row.id) {
            menuItemActions.setMenuItemToDelete(row);
            setShowDeletePopup(true);
        }
    }

    const columns: Array<ColumnType<MenuItemDTO>> = [
        {
            key: "name",
            name: "Name",
            type: "column"
        },
        {
            key: "id",
            name: "Edit",
            type: "action",
            action: onEditClick
        },
        {
            key: "id",
            name: "Delete",
            type: "action",
            action: onDeleteClick
        }
    ];


    return <div className="w-full"><h1>Edit Menu of <b>"{event.name}"</b></h1><br />
        <DataTable rows={menuItems} columns={columns} title="Available Menu Items" onCreateClick={() => navigate("./create/0")} />
        <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={menuItemActions.deleteMenuItem} modalText={'Delete "' + menuItemActions.menuItemToDelete.name + '"'} closeText="Abbrechen" acceptText="Löschen" />
    </div>
}