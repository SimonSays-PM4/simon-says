import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../providers/EventContext";
import { AppContext } from "../../providers/AppContext";
import { getMenuItemService, getMenuService, getOrderService } from "../../api";
import { NotificationType } from "../../enums/NotificationType";
import { MenuDTO, MenuItemDTO, OrderCreateDTO } from "../../gen/api";
import { FieldValues } from "react-hook-form";
import { useNavigate } from "react-router-dom";

type OrderActions = {
    saveOrder: (orderToSave: FieldValues) => void;
    onFormInvalid: (orderToSave: FieldValues) => void;
};

type OrderCreatePageReturnProps = {
    orderActions: OrderActions;
    menuList: MenuDTO[];
    selectedMenus: MenuDTO[];
    setSelectedMenus: React.Dispatch<React.SetStateAction<MenuDTO[]>>;
    menuItemList: MenuItemDTO[];
    selectedMenuItems: MenuItemDTO[];
    setSelectedMenuItems: React.Dispatch<React.SetStateAction<MenuItemDTO[]>>;
    isLoading: boolean;
    isSaving: boolean;
    errorMessage?: string;
};

export const useOrderCreatePage = (): OrderCreatePageReturnProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);
    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isSaving, setIsSaving] = useState<boolean>(false);

    const [menuList, setMenuList] = useState<MenuDTO[]>([]);
    const [selectedMenus, setSelectedMenus] = useState<MenuDTO[]>([]);
    const [menuItemList, setMenuItemList] = useState<MenuItemDTO[]>([]);
    const [selectedMenuItems, setSelectedMenuItems] = useState<MenuItemDTO[]>([]);

    const menuService = getMenuService(appContext.loginInfo.userName, appContext.loginInfo.password);
    const menuItemService = getMenuItemService(appContext.loginInfo.userName, appContext.loginInfo.password);
    const orderService = getOrderService(appContext.loginInfo.userName, appContext.loginInfo.password);

    useEffect(() => {
        reloadMenus();
    }, []);

    const reloadMenus = useCallback(async () => {
        try {
            setIsLoading(true);
            const response = await menuService.getMenus(eventId);
            setMenuList(response.data);

            const responseMenuItem = await menuItemService.getMenuItems(eventId);
            setMenuItemList(responseMenuItem.data);
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, `Beim Laden der Menus ist ein Fehler aufgetreten.`);
        }
        finally {
            setIsLoading(false);
        }
    }, []);

    const onFormInvalid = (data?: FieldValues) => {
        console.log(data);
    };

    const saveOrder = (data: FieldValues) => {
        setIsSaving(true);

        const orderToSave = data as OrderCreateDTO;
        orderToSave.menus = selectedMenus;
        orderToSave.menuItems = selectedMenuItems;

        orderService
            .putOrder(eventId, orderToSave)
            .then((response) => {
                if (response.status === 201 || response.status === 200) {
                    navigate("../");
                } else {
                    setErrorMessage(`Beim Erstellen der Bestellung ist ein Fehler aufgetreten.`);
                }
            })
            .catch(() => {
                setErrorMessage(`Beim Erstellen der Bestellung ist ein Fehler aufgetreten.`);
            })
            .finally(() => {
                setIsSaving(false);
            });
    };

    const orderActions: OrderActions = {
        onFormInvalid,
        saveOrder
    };

    return { orderActions, menuList, selectedMenus, setSelectedMenus, menuItemList, selectedMenuItems, setSelectedMenuItems, isLoading, isSaving, errorMessage };
}