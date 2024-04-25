import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../providers/EventContext";
import { AppContext } from "../../providers/AppContext";
import { getMenuItemService, getMenuService, getOrderService } from "../../api";
import { NotificationType } from "../../enums/NotificationType";
import { OrderCreateDTO, OrderMenuDTO, OrderMenuItemDTO } from "../../gen/api";
import { FieldValues } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { OrderMenuModel } from "../../models/OrderMenuModel";
import { OrderMenuItemModel } from "../../models/OrderMenuItemModel";

type OrderActions = {
    saveOrder: (orderToSave: FieldValues) => void;
    onFormInvalid: (orderToSave: FieldValues) => void;
};

type OrderCreatePageReturnProps = {
    orderActions: OrderActions;
    menuList: OrderMenuDTO[];
    selectedMenus: OrderMenuModel[];
    setSelectedMenus: React.Dispatch<React.SetStateAction<OrderMenuModel[]>>;
    menuItemList: OrderMenuItemDTO[];
    selectedMenuItems: OrderMenuItemModel[];
    setSelectedMenuItems: React.Dispatch<React.SetStateAction<OrderMenuItemModel[]>>;
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

    const [menuList, setMenuList] = useState<OrderMenuDTO[]>([]);
    const [selectedMenus, setSelectedMenus] = useState<OrderMenuModel[]>([]);
    const [menuItemList, setMenuItemList] = useState<OrderMenuItemDTO[]>([]);
    const [selectedMenuItems, setSelectedMenuItems] = useState<OrderMenuItemModel[]>([]);

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
            setMenuList(response.data as OrderMenuDTO[]); // TODO: ugly

            const responseMenuItem = await menuItemService.getMenuItems(eventId);
            setMenuItemList(responseMenuItem.data as OrderMenuItemDTO[]); // TODO: ugly
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