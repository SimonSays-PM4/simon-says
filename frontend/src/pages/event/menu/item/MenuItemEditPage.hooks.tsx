import { useCallback, useContext, useEffect, useState } from "react";
import { getIngredientService, getMenuItemService } from "../../../../api.ts";
import { useNavigate, useParams } from "react-router-dom";
import { IngredientDTO, MenuItemCreateUpdateDTO } from "../../../../gen/api";
import { Control, FieldErrors, FieldValues, UseFormGetValues, UseFormHandleSubmit, UseFormRegister, useForm } from "react-hook-form";
import { EventContext } from "../../../../providers/EventContext.tsx";
import { AppContext } from "../../../../providers/AppContext.tsx";
import { NotificationType } from "../../../../enums/NotificationType.ts";
import { ValueLabel } from "../../../../models/ValueLabel.ts";

type MenuItemEditReturnProps = {
    menuItem: MenuItemCreateUpdateDTO,
    menuItemActions: MenuItemActions,
    isLoading: boolean,
    setShowDeleteModal: (show: boolean) => void,
    showDeleteModal: boolean,
    navigate: (thing: string) => void,
    setIngredients: (ingredients: IngredientDTO[]) => void
    ingredientOptions: ValueLabel[],
    selectedIngredients: ValueLabel[]

    formRegister: UseFormRegister<FieldValues>;
    handleSubmit: UseFormHandleSubmit<FieldValues, undefined>;
    formErrors: FieldErrors<FieldValues>;
    formGetValues: UseFormGetValues<FieldValues>;
    formControl: Control<FieldValues, any>;
};

type MenuItemActions = {
    saveMenuItem: (menuItemToSave: FieldValues) => void,
    onFormInvalid: (data: FieldValues) => void,
    deleteMenuItem: () => void,
}

export const useMenuItemEditPage = (): MenuItemEditReturnProps => {
    const appContext = useContext(AppContext);
    const { eventId } = useContext(EventContext);

    const { menuItemId: id } = useParams();
    const menuItemId = id ? Number(id) : 0;
    const [menuItem, setMenuItem] = useState<MenuItemCreateUpdateDTO>({ id: 0, name: "", price: 0.0, ingredients: [] });

    const [selectedIngredients, setSelectedIngredients] = useState<ValueLabel[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [options, setOptions] = useState<ValueLabel[]>([]);
    const navigate = useNavigate();

    const ingredientService = getIngredientService(appContext.loginInfo.userName, appContext.loginInfo.password);
    const menuItemService = getMenuItemService(appContext.loginInfo.userName, appContext.loginInfo.password);

    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
        setValue,
        control
    } = useForm();

    const loadMenuItem = useCallback(async () => {
        try {
            const items = await ingredientService.listIngredients(eventId);
            const availableIngredients = items.data;
            setIngredients(availableIngredients);
            setOptions([...availableIngredients.map((item) => { return { value: item.id, label: item.name } })]);

            register(nameof<MenuItemCreateUpdateDTO>(e => e.ingredients), { value: [] });

            if (menuItemId && menuItemId > 0) {
                const response = await menuItemService.getMenuItem(eventId, menuItemId);
                const receivedMenuItem = response.data as MenuItemCreateUpdateDTO;
                setMenuItem(receivedMenuItem);

                const receivedIngredients = receivedMenuItem.ingredients.map((item: IngredientDTO) => { return { value: item.id, label: item.name } });
                setValue(nameof<MenuItemCreateUpdateDTO>(e => e.ingredients), Array.from(receivedIngredients));
            }
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, "Beim Laden des Menus ist ein Fehler aufgetreten.");
        }
    }, []);

    useEffect(() => {
        setIsLoading(() => true);
        loadMenuItem()
            .then(() => setIsLoading(() => false));
    }, [eventId, menuItemId])

    const onFormInvalid = (data?: FieldValues) => {
        const newMenuItem = data as MenuItemCreateUpdateDTO;
        newMenuItem.id = menuItemId > 0 ? menuItemId : 0;
        newMenuItem.ingredients = menuItem.ingredients;
        newMenuItem.price = data && data['price'] ? Number(data['price']) : 0;

        setMenuItem(newMenuItem);
    };

    const setIngredients = useCallback((ingredients: IngredientDTO[]) => {
        const newMenuItem = menuItem;
        newMenuItem.ingredients = ingredients;
        setSelectedIngredients(ingredients.map((ing) => { return { value: ing.id, label: ing.name } }));
        setMenuItem(newMenuItem);
    }, [menuItem, menuItemId])

    const saveMenuItem = (data: FieldValues) => {
        setIsLoading(true);

        const menuItemToSave = data as MenuItemCreateUpdateDTO;
        menuItemToSave.id = menuItemId > 0 ? menuItemId : undefined;
        menuItemToSave.ingredients = data["ingredients"].map((ing: ValueLabel) => { return { id: ing.value, name: ing.label } });
        menuItemToSave.price = Number(data["price"]);

        menuItemService
            .putMenuItem(eventId, menuItemToSave)
            .then(() => {
                setIsLoading(false);
                navigate("../menuItem");
                appContext.addNotification(NotificationType.OK, "Saved Menu Item \"" + menuItemToSave.name + "\"")
            })
            .catch(() => {
                setIsLoading(false);
                appContext.addNotification(NotificationType.ERR, "Failed to save \"" + menuItemToSave.name + "\"");
            });
    };

    const deleteMenuItem = useCallback(() => {
        menuItemService.deleteMenuItem(eventId, menuItemId).then(response => {
            if (response.status == 200 || response.status == 201) {
                navigate("./../");
                appContext.addNotification(NotificationType.OK, "Deleted Menu Item");
            }
        })

    }, [menuItem])

    const menuItemActions: MenuItemActions = {
        saveMenuItem: saveMenuItem,
        deleteMenuItem: deleteMenuItem,
        onFormInvalid: onFormInvalid
    };

    return { menuItem, menuItemActions, isLoading, setShowDeleteModal, showDeleteModal, navigate, setIngredients, ingredientOptions: options, selectedIngredients, formErrors: errors, formGetValues: getValues, formRegister: register, formControl: control, handleSubmit }
}