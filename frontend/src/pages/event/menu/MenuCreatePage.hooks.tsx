import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FieldErrors, FieldValues, UseFormGetValues, UseFormHandleSubmit, UseFormRegister, UseFormSetValue, useForm } from "react-hook-form";
import { EventContext } from "../../../providers/EventContext";
import { MenuCreateUpdateDTO, MenuItemDTO } from "../../../gen/api/dist";
import { AppContext } from "../../../providers/AppContext";
import { getMenuItemService, getMenuService } from "../../../api";
import { ValueLabel } from "../../../models/ValueLabel";
import { NotificationType } from "../../../enums/NotificationType";

type MenuActions = {
    deleteMenu: () => void;
    saveMenu: (menuToSave: FieldValues) => void;
    onFormInvalid: (fieldErros: FieldErrors<FieldValues>) => void;
};
type MenuCreateReturnProps = {
    menu: MenuCreateUpdateDTO;
    menuItemOptions: ValueLabel[];
    selectedMenuItemOptions: ValueLabel[];
    setSelectedMenuItemOptions: React.Dispatch<React.SetStateAction<ValueLabel[]>>;
    menuActions: MenuActions;
    isLoading: boolean;
    isSaving: boolean;

    formRegister: UseFormRegister<FieldValues>;
    handleSubmit: UseFormHandleSubmit<FieldValues, undefined>;
    formErrors: FieldErrors<FieldValues>;
    formGetValues: UseFormGetValues<FieldValues>;
    setValue: UseFormSetValue<FieldValues>;
};

export const useMenuCreatePage = (): MenuCreateReturnProps => {
    const appContext = useContext(AppContext);
    const { eventId } = useContext(EventContext);

    const navigate = useNavigate();
    const { menuId: id } = useParams();
    const menuId = id ? Number(id) : 0;

    const [menu, setMenu] = useState<MenuCreateUpdateDTO>({ id: 0, name: "", menuItems: [] });
    const [menuItemOptions, setMenuItemOptions] = useState<ValueLabel[]>([]);
    const [selectedMenuItemOptions, setSelectedMenuItemOptions] = useState<ValueLabel[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [menuItems, setMenuItems] = useState<MenuItemDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const menuService = getMenuService(loginInfo.userName, loginInfo.password);
    const menuItemService = getMenuItemService(loginInfo.userName, loginInfo.password);

    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
        setValue
    } = useForm();

    const loadMenuItem = useCallback(async () => {
        try {
            const items = await menuItemService.getMenuItems(eventId);
            const availableMenuItems = items.data;
            setMenuItems(availableMenuItems);
            setMenuItemOptions([...availableMenuItems.map((item) => { return { value: item.id, label: item.name } })]);

            if (menuId && menuId > 0) {
                const response = await menuService.getMenu(eventId, menuId);
                const receivedMenu = response.data as MenuCreateUpdateDTO;
                setMenu(receivedMenu);
                setSelectedMenuItemOptions([...receivedMenu.menuItems.map((item) => { return { value: item.id, label: item.name } })]);
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
    }, [menuId, eventId]);

    const onFormInvalid = (data: FieldValues) => {
        const formMenuItems = data.menuItems as ValueLabel[];
        const menuToSave = data as MenuCreateUpdateDTO;
        menuToSave.id = menuId > 0 ? menuId : 0;
        menuToSave.menuItems = menuItems.filter((item) => formMenuItems.some((selected) => selected.value === item.id));

        setMenu(menuToSave);
    };

    const saveMenu = useCallback(
        (data: FieldValues) => {
            setIsSaving(true);

            const formMenuItems = data.menuItems as ValueLabel[];
            const menuToSave = data as MenuCreateUpdateDTO;
            menuToSave.id = menuId > 0 ? menuId : undefined;
            menuToSave.menuItems = menuItems.filter((item) => formMenuItems.some((selected) => selected.value === item.id));

            setMenu(menuToSave);

            menuService
                .putMenu(eventId, menuToSave)
                .then((response) => {
                    if (response.status === 201 || response.status === 200) {
                        navigate("../menu");
                        appContext.addNotification(NotificationType.OK, `Menu '${menuToSave.name}' wurde erfolgreich ${menuId > 0 ? "bearbeitet" : "erstellt"}.`);
                    } else {
                        appContext.addNotification(NotificationType.ERR, `Beim ${menuId > 0 ? "Speichern" : "Erstellen"} des Menus ist ein Fehler aufgetreten.`);
                    }
                })
                .catch(() => {
                    appContext.addNotification(NotificationType.ERR, `Beim ${menuId > 0 ? "Speichern" : "Erstellen"} des Menus ist ein Fehler aufgetreten.`);
                })
                .finally(() => {
                    setIsSaving(false);
                });
        },
        [menu]
    );

    const deleteMenu = useCallback(() => {
        if (menuId > 0) {
            setIsSaving(true);

            menuService.deleteMenu(menuId, eventId)
                .then(() => {
                    navigate("/menu");
                    appContext.addNotification(NotificationType.OK, `Menu wurde erfolgreich gelöscht.`);
                })
                .catch(() => {
                    appContext.addNotification(NotificationType.ERR, `Beim Löschen des Menus ist ein Fehler aufgetreten.`);
                })
                .finally(() => {
                    setIsSaving(false);
                });
        }
    }, [id]);

    const menuActions: MenuActions = {
        saveMenu,
        deleteMenu,
        onFormInvalid
    };

    return { menu, selectedMenuItemOptions, setSelectedMenuItemOptions, menuItemOptions, menuActions, isLoading, isSaving, formErrors: errors, formGetValues: getValues, formRegister: register, handleSubmit, setValue };
};
