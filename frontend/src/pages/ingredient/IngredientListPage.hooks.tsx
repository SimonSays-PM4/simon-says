import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../providers/EventContext";
import { getIngredientService } from "../../api";
import { IngredientDTO } from "../../gen/api";
import { AppContext } from "../../providers/AppContext";
import { NotificationType } from "../../enums/NotificationType";

type IngredientActions = {
    deleteIngredient: () => void,
    setIngredientToDelete: (ingredient: IngredientDTO) => void,
    ingredientToDelete: IngredientDTO
};
type IngredientListPageReturnProps = {
    ingredientActions: IngredientActions
    isLoading: boolean,
    showDeletePopup: boolean
    setShowDeletePopup: (show: boolean) => void,
    data: IngredientDTO[]
};

export const useIngredientListPage = (): IngredientListPageReturnProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);

    const [ingredientToDelete, setIngredientToDelete] = useState<IngredientDTO>({ id: 0, name: "", mustBeProduced: false });
    const [isLoading, setIsLoading] = useState(false);
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [data, setData] = useState<IngredientDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const ingredientService = getIngredientService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (!showDeletePopup) {
            reloadIngredients();
        }
    }, [showDeletePopup]);

    const reloadIngredients = useCallback(() => {
        try {
            setIsLoading(true);
            ingredientService.listIngredients(eventId).then((response) => {
                setData(response.data);
            });
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, `Beim Laden der Zutaten ist ein Fehler aufgetreten.`);
        }
        finally {
            setIsLoading(false);
        }
    }, []);

    const deleteIngredient = useCallback(() => {
        try {
            if (ingredientToDelete.id && ingredientToDelete.id > 0) {
                setIsLoading(true);

                ingredientService.deleteIngredient(ingredientToDelete.id, eventId).then(() => {
                    appContext.addNotification(NotificationType.OK, `Zutat wurde gelöscht.`);
                    setShowDeletePopup(false);
                });
            }
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, `Beim Löschen der Zutate ist ein Fehler aufgetreten.`);
        }
        finally {
            setIsLoading(false);
        }
    }, [ingredientToDelete.id]);

    const ingredientActions: IngredientActions = {
        deleteIngredient,
        setIngredientToDelete,
        ingredientToDelete
    };

    return { ingredientActions, isLoading, showDeletePopup, setShowDeletePopup, data };
}