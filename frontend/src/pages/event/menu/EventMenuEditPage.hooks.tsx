import { useCallback, useContext, useEffect, useState } from "react";
import { getIngredientService, getMenuItemService } from "../../../api.ts";
import { useNavigate, useParams } from "react-router-dom";
import { IngredientDTO, MenuItemCreateUpdateDTO, MenuItemDTO } from "../../../gen/api";
import { FieldValues } from "react-hook-form";
import { EventContext } from "../../../providers/EventContext.tsx";
import { AppContext } from "../../../providers/AppContext.tsx";
import { NotificationType } from "../../../enums/NotificationType.ts";
import { ValueLabel } from "../../../models/ValueLabel.ts";

type EventMenuEditReturnProps = {
    menuItem: MenuItemDTO,
    menuItemActions: MenuItemActions,
    isLoading: boolean,
    setShowDeleteModal: (show: boolean) => void,
    showDeleteModal: boolean,
    navigate: (thing: string) => void,
    setIngredients: (ingredients: IngredientDTO[]) => void
    ingredientOptions: ValueLabel[],
    selectedIngredients: ValueLabel[]
};

type MenuItemActions = {
    saveMenuItem: (menuItemToSave: FieldValues) => void,
    deleteMenuItem: () => void,
}

export const useEventMenuEditPage = (): EventMenuEditReturnProps => {
    const appContext = useContext(AppContext);
    const { eventId } = useContext(EventContext);

    const { menuItemId: id } = useParams();
    const menuItemId = id ? Number(id) : 0;

    const [menuItem, setMenuItem] = useState<MenuItemDTO>({ id: 0, name: "", price: 0.0, ingredients: [] });
    const [selectedIngredients, setSelectedIngredients] = useState<ValueLabel[]>([]);
    const [isLoading, setLoading] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [options, setOptions] = useState<ValueLabel[]>([]);
    const navigate = useNavigate();

    const ingredientService = getIngredientService(appContext.loginInfo.userName, appContext.loginInfo.password);
    const menuItemService = getMenuItemService(appContext.loginInfo.userName, appContext.loginInfo.password);

    useEffect(() => {
        console.log(menuItemId)
        if (eventId > 0) {
            if (menuItemId > 0) {
                menuItemService.getMenuItem(eventId, menuItemId).then((item) => {
                    setSelectedIngredients(item.data.ingredients.map((ing) => {
                        return { value: ing.id, label: ing.name }
                    }))
                    setMenuItem(item.data)
                }).catch(() => appContext.addNotification(NotificationType.ERR, "Failed to Fetch Menu Item"))
            }
            ingredientService.listIngredients(eventId).then((ingredients) => {
                setOptions([...ingredients.data.map((ing) => { return { value: ing.id, label: ing.name } })]);
            }).catch(() => appContext.addNotification(NotificationType.ERR, "Failed to Fetch Ingredients"))
        }
    }, [eventId, menuItemId])

    const setIngredients = useCallback((ingredients: IngredientDTO[]) => {
        const newMenuItem = menuItem;
        newMenuItem.ingredients = ingredients;
        setSelectedIngredients(ingredients.map((ing) => { return { value: ing.id, label: ing.name } }))
        setMenuItem(newMenuItem)
    }, [menuItem, menuItemId])

    const saveMenuItem = useCallback(
        (data: FieldValues) => {
            setLoading(true);

            const menuItemToSave = data as MenuItemCreateUpdateDTO;
            menuItemToSave.id = menuItemId > 0 ? menuItemId : undefined;
            menuItemToSave.ingredients = menuItem.ingredients;

            menuItemService
                .putMenuItem1(eventId, menuItemToSave)
                .then((response) => {
                    setLoading(false);
                    navigate("./" + response.data.id);
                    appContext.addNotification(NotificationType.OK, "Saved Menu Item \"" + menuItemToSave.name + "\"")
                })
                .catch(() => {
                    setLoading(false);
                    appContext.addNotification(NotificationType.ERR, "Failed to save \"" + menuItemToSave.name + "\"");
                });
        },
        [menuItem]
    );

    const deleteMenuItem = useCallback(() => {
        menuItemService.deleteMenuItem1(eventId, menuItemId).then(response => {
            if (response.status == 200 || response.status == 201) {
                navigate("./../")
                appContext.addNotification(NotificationType.OK, "Deleted Menu Item")
            }
        })

    }, [menuItem])

    const menuItemActions: MenuItemActions = {
        saveMenuItem: saveMenuItem,
        deleteMenuItem: deleteMenuItem
    };

    return { menuItem, menuItemActions, isLoading, setShowDeleteModal, showDeleteModal, navigate, setIngredients, ingredientOptions: options, selectedIngredients }
}