import React from "react";
import { useMenuItemPage } from "./MenuItemPage.hooks.tsx";
import { Loader } from "../../../../components/Loader.tsx";
import { ColumnType } from "../../../../models/ColumnType.ts";
import { MenuItemDTO } from "../../../../gen/api";
import { DataTable } from "../../../../components/data-tables/DataTable.tsx";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../../../components/Popup.tsx";
import {MdEditSquare} from "react-icons/md";
import {FaRegTrashAlt} from "react-icons/fa";
import {PiHamburgerDuotone} from "react-icons/pi";

export const MenuItemPage: React.FC = () => {
    const { menuItems, isLoading, menuItemActions, showDeletePopup, setShowDeletePopup } = useMenuItemPage();
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
            name: "Bearbeiten",
            elementKey: "editAction",
            type: "action",
            children: <MdEditSquare/>,
            noText:true,
            action: onEditClick
        },
        {
            key: "id",
            name: "Löschen",
            elementKey:"deleteAction",
            type: "action",
            children: <FaRegTrashAlt/>,
            noText:true,
            action: onDeleteClick
        },
    ];


    return <div className="w-full">
        <DataTable rows={menuItems} columns={columns} title={"Menu Items"} icon={<PiHamburgerDuotone/>} onCreateClick={() => navigate("./create/0")} onBackClick={() => navigate("../../events")} />
        <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={menuItemActions.deleteMenuItem} modalText={'Delete "' + menuItemActions.menuItemToDelete.name + '"'} closeText="Abbrechen" acceptText="Löschen" />
    </div>
}