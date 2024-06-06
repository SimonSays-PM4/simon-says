import React from "react";
import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { Loader } from "../../components/Loader";
import { IngredientDTO, IngredientCreateUpdateDTO } from "../../gen/api";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../components/Popup.tsx";
import { useIngredientListPage } from "./IngredientListPage.hooks.tsx";
import { MdEditSquare } from "react-icons/md";
import { FaRegTrashAlt } from "react-icons/fa";
import { PiCookieDuotone } from "react-icons/pi";

export const IngredientListPageComponent: React.FC = () => {
    const { isLoading, ingredientActions, showDeletePopup, setShowDeletePopup, data } = useIngredientListPage();
    const navigate = useNavigate();

    const onEditClick = (row: IngredientCreateUpdateDTO) => {
        navigate(`../ingredient/create/` + row.id);
    };

    const onDeleteClick = (row: IngredientDTO) => {
        ingredientActions.setIngredientToDelete(row);
        setShowDeletePopup(true);
    };

    const columns: Array<ColumnType<IngredientDTO>> = [
        {
            key: "name",
            name: "Name",
            type: "column"
        }, {
            key: "mustBeProduced",
            name: "Muss produziert werden",
            type: "column",
            formatter: (ingredient) => `${ingredient.mustBeProduced ? "Ja" : "Nein"}`,
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

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<IngredientDTO> icon={<PiCookieDuotone />} title="Zutaten" columns={columns} rows={data} onCreateClick={() => navigate(`../ingredient/create`)} onBackClick={() => navigate("../../events")} />
            )}

            <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={ingredientActions.deleteIngredient} modalText={'Zutat "' + ingredientActions.ingredientToDelete.name + '" löschen?'} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}