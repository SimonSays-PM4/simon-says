import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FieldValues } from "react-hook-form";
import { EventContext } from "../../../providers/EventContext";
import { MenuCreateUpdateDTO, MenuItemDTO } from "../../../gen/api/dist";
import { AppContext } from "../../../providers/AppContext";
import { getMenuItemService, getMenuService } from "../../../api";
import { ValueLabel } from "../../../models/ValueLabel";
import { NotificationType } from "../../../enums/NotificationType";

type MenuActions = {
    deleteMenu: () => void;
    saveMenu: (menuToSave: FieldValues) => void;
    onFormInvalid: (menu: FieldValues) => void;
};
type MenuCreateReturnProps = {
    menu: MenuCreateUpdateDTO;
    menuItemOptions: ValueLabel[];
    selectedMenuItemOptions: ValueLabel[];
    setSelectedMenuItemOptions: React.Dispatch<React.SetStateAction<ValueLabel[]>>;
    menuActions: MenuActions;
    isLoading: boolean;
    isSaving: boolean;
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
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [menuItems, setMenuItems] = useState<MenuItemDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const menuService = getMenuService(loginInfo.userName, loginInfo.password);
    const menuItemService = getMenuItemService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (menuId && menuId > 0) {
            setIsLoading(true);

            menuService
                .getMenu(eventId, menuId)
                .then((response) => {
                    const receivedMenu = response.data as MenuCreateUpdateDTO;
                    setMenu(receivedMenu);
                    setSelectedMenuItemOptions([...receivedMenu.menuItems.map((item) => { return { value: item.id, label: item.name } })]);
                    appContext.addNotification(NotificationType.OK, "Menu wurde erfolgreich geladen.");
                })
                .catch(() => {
                    appContext.addNotification(NotificationType.ERR, "Beim Laden des Menus ist ein Fehler aufgetreten.");
                })
                .finally(() => {
                    setIsLoading(false);
                });
        }

        menuItemService.getMenuItems(eventId)
            .then((items) => {
                const availableMenuItems = items.data;
                setMenuItemOptions([...availableMenuItems.map((item) => { return { value: item.id, label: item.name } })]);
                setMenuItems(availableMenuItems);
            });
    }, [id]);

    const onFormInvalid = (data?: FieldValues) => {
        const menuToSave = data as MenuCreateUpdateDTO;
        menuToSave.id = menuId > 0 ? menuId : 0;
        menuToSave.menuItems = menuItems.filter((item) => selectedMenuItemOptions.some((selected) => selected.value === item.id));

        setMenu(menuToSave);
    };

    const saveMenu = useCallback(
        (data: FieldValues) => {
            setIsSaving(true);

            const menuToSave = data as MenuCreateUpdateDTO;
            menuToSave.id = menuId > 0 ? menuId : undefined;
            menuToSave.menuItems = menuItems.filter((item) => selectedMenuItemOptions.some((selected) => selected.value === item.id));

            setMenu(menuToSave);

            menuService
                .putMenu(eventId, menuToSave)
                .then((response) => {
                    if (response.status === 201 || response.status === 200) {
                        navigate("../menu");
                        appContext.addNotification(NotificationType.OK, `Menu '${menuToSave.name}' wurde erfolgreich gelöscht.`);
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

    return { menu, setSelectedMenuItemOptions, selectedMenuItemOptions, menuItemOptions, menuActions, isLoading, isSaving };
};
