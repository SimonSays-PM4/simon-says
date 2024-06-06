import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Control, FieldErrors, FieldValues, UseFormGetValues, UseFormHandleSubmit, UseFormRegister, useForm } from "react-hook-form";
import { EventContext } from "../../../providers/EventContext";
import { AppContext } from "../../../providers/AppContext";
import { getMenuItemService, getMenuService } from "../../../api";
import { ValueLabel } from "../../../models/ValueLabel";
import { NotificationType } from "../../../enums/NotificationType";
import { MenuCreateUpdateDTO } from "../../../gen/api";
import { MenuItemDTO } from "../../../gen/api";

type MenuActions = {
    deleteMenu: () => void;
    saveMenu: (menuToSave: FieldValues) => void;
    onFormInvalid: (fieldErros: FieldErrors<FieldValues>) => void;
};
type MenuCreateReturnProps = {
    menu: MenuCreateUpdateDTO;
    menuItemOptions: ValueLabel[];
    selectedMenuItemOptions: ValueLabel[];
    menuActions: MenuActions;
    isLoading: boolean;
    isSaving: boolean;

    formRegister: UseFormRegister<FieldValues>;
    handleSubmit: UseFormHandleSubmit<FieldValues, undefined>;
    formErrors: FieldErrors<FieldValues>;
    formGetValues: UseFormGetValues<FieldValues>;
    formControl: Control<FieldValues, any>;
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
        setValue,
        control
    } = useForm();

    const loadMenuItem = useCallback(async () => {
        try {
            const items = await menuItemService.getMenuItems(eventId);
            const availableMenuItems = items.data;
            setMenuItems(availableMenuItems);
            setMenuItemOptions([...availableMenuItems.map((item) => { return { value: item.id, label: item.name } })]);

            register(nameof<MenuCreateUpdateDTO>(e => e.menuItems), { value: [] });

            if (menuId && menuId > 0) {
                const response = await menuService.getMenu(eventId, menuId);
                const receivedMenu = response.data as MenuCreateUpdateDTO;
                setMenu(receivedMenu);

                const receivedMenuItems = receivedMenu.menuItems.map((item: MenuItemDTO) => { return { value: item.id, label: item.name } });
                setSelectedMenuItemOptions(Array.from(receivedMenuItems));
                setValue(nameof<MenuCreateUpdateDTO>(e => e.menuItems), Array.from(receivedMenuItems));
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

    const saveMenu = (data: FieldValues) => {
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
    };

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

    return { menu, selectedMenuItemOptions, menuItemOptions, menuActions, isLoading, isSaving, formErrors: errors, formGetValues: getValues, formRegister: register, formControl: control, handleSubmit };
};
