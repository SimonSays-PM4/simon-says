import React from "react";
import { DataTable } from "../../../components/data-tables/DataTable.tsx";
import { ColumnType } from "../../../models/ColumnType.ts";
import { Loader } from "../../../components/Loader.tsx";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../../components/Popup.tsx";
import { useMenuListPage } from "./MenuListPage.hooks.tsx";
import { MenuDTO } from "../../../gen/api/api.ts";
import { MenuDisplayModel } from "../../../models/MenuDisplayModel.ts";

export const MenuListPageComponent: React.FC = () => {
    const { isLoading, menuActions, showDeletePopup, setShowDeletePopup, menuList } = useMenuListPage();
    const navigate = useNavigate();

    const onEditClick = (row: MenuDTO) => {
        navigate(`../menu/create/` + row.id);
    };

    const onDeleteClick = (row: MenuDTO) => {
        menuActions.setMenuToDelete(row);
        setShowDeletePopup(true);
    };

    const columns: Array<ColumnType<MenuDisplayModel>> = [
        {
            key: "name",
            name: "Name",
            type: "column"
        },
        {
            key: "price",
            name: "Preis",
            type: "column"
        },
        {
            key: "menuItemsString",
            name: "Menu Items",
            type: "column"
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

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<MenuDisplayModel> title="Menus" columns={columns} rows={menuList.map((menu) => new MenuDisplayModel(menu.id, menu.name, menu.menuItems, menu.price, menu.menuItems.map((item) => item.name).join(", ")))} onCreateClick={() => navigate(`../menu/create`)} />
            )}

            <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={menuActions.deleteMenu} modalText={'Menu "' + menuActions.menuToDelete.name + '" löschen?'} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}