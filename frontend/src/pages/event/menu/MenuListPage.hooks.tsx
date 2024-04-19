import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../../providers/EventContext";
import { MenuDTO } from "../../../gen/api/dist";
import { AppContext } from "../../../providers/AppContext";
import { getMenuService } from "../../../api";
import { NotificationType } from "../../../enums/NotificationType";

type MenuActions = {
    deleteMenu: () => void,
    setMenuToDelete: (ingredient: MenuDTO) => void,
    menuToDelete: MenuDTO
};
type MenuListPageReturnProps = {
    menuActions: MenuActions
    isLoading: boolean,
    showDeletePopup: boolean
    setShowDeletePopup: (show: boolean) => void,
    menuList: MenuDTO[]
};

export const useMenuListPage = (): MenuListPageReturnProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);

    const [menuToDelete, setMenuToDelete] = useState<MenuDTO>({ id: 0, name: "", menuItems: [], price: 0 });
    const [isLoading, setIsLoading] = useState(false);
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [menuList, setMenuList] = useState<MenuDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const menuService = getMenuService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (!showDeletePopup) {
            reloadMenus();
        }
    }, [showDeletePopup]);

    const reloadMenus = useCallback(() => {
        try {
            setIsLoading(true);
            menuService.getMenus(eventId).then((response) => {
                setMenuList(response.data);
            });
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, `Beim Laden der Menus ist ein Fehler aufgetreten.`);
        }
        finally {
            setIsLoading(false);
        }
    }, []);

    const deleteMenu = useCallback(() => {
        try {
            if (menuToDelete.id && menuToDelete.id > 0) {
                setIsLoading(true);

                menuService.deleteMenu(eventId, menuToDelete.id).then(() => {
                    setShowDeletePopup(false);
                });
            }
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, `Beim Löschen des Menus ist ein Fehler aufgetreten.`);
        }
        finally {
            setIsLoading(false);
        }
    }, [menuToDelete.id]);

    const menuActions: MenuActions = {
        deleteMenu,
        setMenuToDelete,
        menuToDelete
    };

    return { menuActions, isLoading, showDeletePopup, setShowDeletePopup, menuList };
}