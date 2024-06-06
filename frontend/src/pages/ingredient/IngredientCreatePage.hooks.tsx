import {IngredientCreateUpdateDTO} from "../../gen/api";
import {useCallback, useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {FieldValues} from "react-hook-form";
import {EventContext} from "../../providers/EventContext";
import {getIngredientService} from "../../api";
import {AppContext} from "../../providers/AppContext";
import {NotificationType} from "../../enums/NotificationType.ts";

type IngredientActions = {
    deleteIngredient: () => void;
    saveIngredient: (ingredientToSave: FieldValues) => void;
    onFormInvalid: (ingredient: FieldValues) => void;
};
type IngredientCreateReturnProps = {
    ingredient: IngredientCreateUpdateDTO;
    errorMessage: string | undefined;
    ingredientActions: IngredientActions;
    isLoading: boolean;
    isSaving: boolean;
};

export const useIngredientCreatePage = (): IngredientCreateReturnProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);
    const navigate = useNavigate();
    const { id } = useParams();
    const ingredientId = id ? Number(id) : 0;

    const [ingredient, setIngredient] = useState<IngredientCreateUpdateDTO>({ id: 0, name: "", mustBeProduced: false });
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isSaving, setIsSaving] = useState<boolean>(false);

    const { loginInfo } = useContext(AppContext);
    const ingredientService = getIngredientService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (ingredientId && ingredientId > 0) {
            setIsLoading(true);

            ingredientService
                .getIngredient(ingredientId, eventId)
                .then((response) => {
                    const receivedIngredient = response.data as IngredientCreateUpdateDTO;
                    setIngredient(receivedIngredient);
                })
                .catch(() => {
                    setErrorMessage("Beim Laden der Zutat ist ein Fehler aufgetreten.");
                })
                .finally(() => {
                    setIsLoading(false);
                });
        }
    }, [id]);

    const onFormInvalid = (data?: FieldValues) => {
        const ingredientToSave = data as IngredientCreateUpdateDTO;
        ingredientToSave.id = ingredientId > 0 ? ingredientId : 0;
        setIngredient(ingredientToSave);
    };

    const saveIngredient = useCallback(
        (data: FieldValues) => {
            setIsSaving(true);

            const ingredientToSave = data as IngredientCreateUpdateDTO;
            ingredientToSave.id = ingredientId > 0 ? ingredientId : undefined;
            ingredientToSave.mustBeProduced = ingredientToSave.mustBeProduced ?? false;
            setIngredient(ingredientToSave);

            ingredientService
                .createIngredient(eventId, ingredientToSave)
                .then((response) => {
                    if (response.status === 201 || response.status === 200) {
                        navigate("../ingredients");
                        appContext.addNotification(NotificationType.OK, `Die Zutat wurde ${ingredientId > 0 ? "gespeichert" : "erstellt"}`)
                    } else {
                        setErrorMessage(`Beim ${ingredientId > 0 ? "Speichern" : "Erstellen"} der Zutat ist ein Fehler aufgetreten.`);
                    }
                })
                .catch(() => {
                    appContext.addNotification(NotificationType.ERR, `Beim ${ingredientId > 0 ? "Speichern" : "Erstellen"} der Zutat ist ein Fehler aufgetreten.`)
                    setErrorMessage(`Beim ${ingredientId > 0 ? "Speichern" : "Erstellen"} der Zutat ist ein Fehler aufgetreten.`);
                })
                .finally(() => {
                    setIsSaving(false);
                });
        },
        [ingredient]
    );

    const deleteIngredient = useCallback(() => {
        if (ingredientId > 0) {
            setIsSaving(true);

            ingredientService.deleteIngredient(ingredientId, eventId)
                .then(() => {
                    navigate("/ingredients");
                    appContext.addNotification(NotificationType.OK, `Zutat wurde gelöscht`)
                })
                .catch(() => {
                    appContext.addNotification(NotificationType.ERR, `Beim Löschen der Zutat ist ein Fehler aufgetreten.`)
                    setErrorMessage("Beim Löschen der Zutat ist ein Fehler aufgetreten.");
                })
                .finally(() => {
                    setIsSaving(false);
                });
        }
    }, [id]);

    const ingredientActions: IngredientActions = {
        saveIngredient,
        deleteIngredient,
        onFormInvalid
    };

    return { ingredient, errorMessage, ingredientActions, isLoading, isSaving };
};
