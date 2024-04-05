import { useCallback, useEffect, useState } from "react";
import { IngredientControllerApi, IngredientDTO } from "../../gen/api";

type IngredientActions = {
    deleteIngredient: () => void,
    setIngredientToDelete: (ingredient: IngredientDTO) => void,
    ingredientToDelete: IngredientDTO
};
type IngredientListPageReturnProps = {
    eventActions: IngredientActions
    isLoading: boolean,
    showDeletePopup: boolean
    setShowDeletePopup: (show: boolean) => void,
    data: IngredientDTO[]
};

export const useIngredientListPage = (): IngredientListPageReturnProps => {
    const [ingredientToDelete, setIngredientToDelete] = useState<IngredientDTO>({ id: 0, name: "" });
    const [isLoading, setIsLoading] = useState(false);
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [data, setData] = useState<IngredientDTO[]>([]);

    const ingredientControllerApi = new IngredientControllerApi();

    useEffect(() => {
        if (!showDeletePopup) {
            reloadIngredients();
        }
    }, [showDeletePopup]);

    const reloadIngredients = useCallback(() => {
        try {
            setIsLoading(true);
            ingredientControllerApi.listIngredients().then((response) => {
                setData(response.data);
            });
        }
        catch (error) {
            // TOOD: handle error
        }
        finally {
            setIsLoading(false);
        }
    }, []);

    const deleteIngredient = useCallback(() => {
        try {
            if (ingredientToDelete.id && ingredientToDelete.id > 0) {
                setIsLoading(true);

                ingredientControllerApi.deleteIngredient(ingredientToDelete.id).then(() => {
                    setShowDeletePopup(false);
                });
            }
        }
        catch (error) {
            // TOOD: handle error
        }
        finally {
            setIsLoading(false);
        }
    }, [ingredientToDelete.id]);

    const eventActions: IngredientActions = {
        deleteIngredient,
        setIngredientToDelete,
        ingredientToDelete
    };

    return { eventActions, isLoading, showDeletePopup, setShowDeletePopup, data };
}