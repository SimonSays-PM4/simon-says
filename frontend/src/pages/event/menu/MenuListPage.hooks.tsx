import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../../providers/EventContext";
import { MenuDTO } from "../../../gen/api/dist";
import { AppContext } from "../../../providers/AppContext";
import { getMenuService } from "../../../api";

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
    data: MenuDTO[]
};

export const useMenuListPage = (): MenuListPageReturnProps => {
    const { eventId } = useContext(EventContext);
    const [menuToDelete, setMenuToDelete] = useState<MenuDTO>({ id: 0, name: "", menuItems: [], price: 0 });
    const [isLoading, setIsLoading] = useState(false);
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [data, setData] = useState<MenuDTO[]>([]);

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
                setData(response.data);
            });
        }
        catch (error) {
            // TOOD: handle error
        }
        finally {
            setIsLoading(false);
        }
    }, []);

    const deleteMenu = useCallback(() => {
        try {
            if (menuToDelete.id && menuToDelete.id > 0) {
                setIsLoading(true);

                menuService.deleteMenu(menuToDelete.id, eventId).then(() => {
                    setShowDeletePopup(false);
                });
            }
        }
        catch (error) {
            // TOOD: handle error
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

    return { menuActions, isLoading, showDeletePopup, setShowDeletePopup, data };
}