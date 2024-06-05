import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../providers/EventContext";
import { AppContext } from "../../providers/AppContext";
import { getMenuItemService, getMenuService, getOrderService } from "../../api";
import { NotificationType } from "../../enums/NotificationType";
import { MenuDTO, MenuItemDTO, OrderCreateDTO } from "../../gen/api";
import { FieldValues } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { OrderMenuModel } from "../../models/OrderMenuModel";
import { OrderMenuItemModel } from "../../models/OrderMenuItemModel";

type OrderActions = {
    saveOrder: (orderToSave: FieldValues) => void;
};

type OrderCreatePageReturnProps = {
    orderActions: OrderActions;
    menuList: MenuDTO[];
    selectedMenus: OrderMenuModel[];
    setSelectedMenus: React.Dispatch<React.SetStateAction<OrderMenuModel[]>>;
    menuItemList: MenuItemDTO[];
    selectedMenuItems: OrderMenuItemModel[];
    setSelectedMenuItems: React.Dispatch<React.SetStateAction<OrderMenuItemModel[]>>;
    isTakeAway: boolean;
    setIsTakeAway: React.Dispatch<React.SetStateAction<boolean>>;
    isLoading: boolean;
    isSaving: boolean;
    errorMessage?: string;
};

export const useOrderCreatePage = (): OrderCreatePageReturnProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);
    const navigate = useNavigate();

    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isSaving, setIsSaving] = useState<boolean>(false);

    const [menuList, setMenuList] = useState<MenuDTO[]>([]);
    const [selectedMenus, setSelectedMenus] = useState<OrderMenuModel[]>([]);
    const [menuItemList, setMenuItemList] = useState<MenuItemDTO[]>([]);
    const [selectedMenuItems, setSelectedMenuItems] = useState<OrderMenuItemModel[]>([]);
    const [isTakeAway, setIsTakeAway] = useState<boolean>(false);
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);

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

    const saveOrder = (data: FieldValues) => {
        setIsSaving(true);

        const orderToSave = data as OrderCreateDTO;
        orderToSave.menus = selectedMenus;
        orderToSave.menuItems = selectedMenuItems;
        orderToSave.takeAway = isTakeAway;

        if (!isTakeAway && !orderToSave.tableNumber) {
            setErrorMessage("Tischnummer muss angegeben werden");
            setIsSaving(false);
            return;
        }

        orderService
            .putOrder(eventId, orderToSave)
            .then((response) => {
                if (response.status === 201 || response.status === 200) {
                    navigate("../");
                } else {
                    appContext.addNotification(NotificationType.ERR, `Beim Erstellen der Bestellung ist ein Fehler aufgetreten.`);
                }
            })
            .catch((e) => {
                console.log(e);
                if (e.response.status === 400) {
                    setErrorMessage("Error");
                    appContext.addNotification(NotificationType.ERR, `Die Tischnummer ist ungültig.`);
                } else {
                    appContext.addNotification(NotificationType.ERR, `Beim Erstellen der Bestellung ist ein Fehler aufgetreten.`);
                }
            })
            .finally(() => {
                setIsSaving(false);
            });
    };

    const orderActions: OrderActions = {
        saveOrder
    };

    return { orderActions, menuList, selectedMenus, setSelectedMenus, menuItemList, selectedMenuItems, setSelectedMenuItems, isTakeAway, setIsTakeAway, isLoading, isSaving, errorMessage };
}