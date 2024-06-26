import { getEventService, getMenuItemService } from "../../../api.ts";
import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../../providers/EventContext.tsx";
import { AppContext } from "../../../providers/AppContext.tsx";
import { NotificationType } from "../../../enums/NotificationType.ts";
import { EventDTO, MenuItemDTO } from "../../../gen/api";

type MenuItemActions = {
    setMenuItemToDelete: (menuItem: MenuItemDTO) => void;
    menuItemToDelete: MenuItemDTO;
    deleteMenuItem: () => void;
};

type MenuItemReturnParams = {
    menuItems: MenuItemDTO[];
    isLoading: boolean;
    event: EventDTO;
    menuItemActions: MenuItemActions;
    setShowDeletePopup: (show: boolean) => void;
    showDeletePopup: boolean;
};

export const useMenuItemPage = (): MenuItemReturnParams => {
    const { eventId } = useContext(EventContext);
    const [menuItemToDelete, setMenuItemToDelete] = useState<MenuItemDTO>({
        id: 0,
        name: "",
        price: 0.0,
        ingredients: []
    });
    const [menuItems, setMenuItems] = useState<MenuItemDTO[]>([]);
    const [event, setEvent] = useState<EventDTO>({ id: 0, password: "", name: "", numberOfTables: 0 });
    const [isLoading, setLoading] = useState<boolean>(false);
    const [showDeletePopup, setShowDeletePopup] = useState<boolean>(false);
    const appContext = useContext(AppContext);

    const eventService = getEventService(appContext.loginInfo.userName, appContext.loginInfo.password);
    const menuItemService = getMenuItemService(appContext.loginInfo.userName, appContext.loginInfo.password);

    useEffect(() => {
        setLoading(true);
        menuItemService.getMenuItems(eventId).then((items) => {
            setMenuItems(items.data);
            setLoading(false);
        });
    }, [eventId, showDeletePopup]);

    useEffect(() => {
        setLoading(true);
        eventService.getEvent(eventId).then((data) => {
            setEvent(data.data);
            setLoading(false);
        });
    }, [eventId]);

    const deleteMenuItem = useCallback(() => {
        setLoading(true);
        menuItemService.deleteMenuItem(eventId, menuItemToDelete.id).then(() => {
            appContext.addNotification(NotificationType.OK, "Menüpunkt wurde gelöscht.");
            setShowDeletePopup(false);
            setLoading(false);
        }).catch(_ => {
            appContext.addNotification(NotificationType.ERR, "Menüpunkt konnte nicht gelöscht werden.");
        });
    }, [eventId, menuItemToDelete]);

    const menuItemActions = {
        setMenuItemToDelete,
        menuItemToDelete,
        deleteMenuItem
    };

    return {
        menuItems,
        isLoading,
        event,
        menuItemActions,
        showDeletePopup,
        setShowDeletePopup
    };
};
