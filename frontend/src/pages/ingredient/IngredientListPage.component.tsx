import React from "react";
import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { Loader } from "../../components/Loader";
import { IngredientDTO, IngredientCreateUpdateDTO } from "../../gen/api";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../components/Popup.tsx";
import { useIngredientListPage } from "./IngredientListPage.hooks.tsx";

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
            type:"column"
        },
        {
            key:"id",
            name:"Bearbeiten",
            type:"action",
            action: onEditClick
        },
        {
            key:"id",
            name:"Löschen",
            type:"action",
            action: onDeleteClick
        }
    ];

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<IngredientDTO> title="Zutaten" columns={columns} rows={data} onCreateClick={() => navigate(`../ingredient/create`)}  />
            )}

            <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={ingredientActions.deleteIngredient} modalText={'Zutate "' + ingredientActions.ingredientToDelete.name + '" löschen?'} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}