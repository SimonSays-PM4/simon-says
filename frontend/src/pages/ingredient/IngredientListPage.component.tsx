import React from "react";
import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { Loader } from "../../components/Loader";
import { IngredientDTO, IngredientCreateUpdateDTO } from "../../gen/api";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../components/Popup.tsx";
import { useIngredientListPage } from "./IngredientListPage.hooks.tsx";

export const IngredientListPageComponent: React.FC = () => {
    const { isLoading, eventActions, showDeletePopup, setShowDeletePopup, data } = useIngredientListPage();
    const navigate = useNavigate();

    const columns: Array<ColumnType<IngredientDTO>> = [
        {
            key: "name",
            name: "Name",
        },
    ];

    const onEditClick = (row: IngredientCreateUpdateDTO) => {
        navigate("../ingredient/create/" + row.id);
    };

    const onDeleteClick = (row: IngredientDTO) => {
        eventActions.setIngredientToDelete(row);
        setShowDeletePopup(true);
    };

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<IngredientDTO> title="Zutaten" columns={columns} rows={data} onCreateClick={() => navigate("/ingredient/create")} onEditClick={onEditClick} onDeleteClick={onDeleteClick} />
            )}

            <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={eventActions.deleteIngredient} modalText={'Zutate "' + eventActions.ingredientToDelete.name + '" löschen?'} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}