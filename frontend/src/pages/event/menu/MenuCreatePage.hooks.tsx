import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FieldValues } from "react-hook-form";
import { EventContext } from "../../../providers/EventContext";
import { MenuCreateUpdateDTO, MenuItemDTO } from "../../../gen/api/dist";
import { AppContext } from "../../../providers/AppContext";
import { getMenuService } from "../../../api";

type MenuActions = {
    deleteMenu: () => void;
    saveMenu: (menuToSave: FieldValues) => void;
    onFormInvalid: (menu: FieldValues) => void;
    setSelectedMenuItems: React.Dispatch<React.SetStateAction<MenuItemDTO[]>>;
};
type MenuCreateReturnProps = {
    menu: MenuCreateUpdateDTO;
    selectedMenuItems: MenuItemDTO[];
    errorMessage: string | undefined;
    menuActions: MenuActions;
    isLoading: boolean;
    isSaving: boolean;
};

export const useMenuCreatePage = (): MenuCreateReturnProps => {
    const { eventId } = useContext(EventContext);
    const navigate = useNavigate();
    const { id } = useParams();
    const menuId = id ? Number(id) : 0;

    const [menu, setMenu] = useState<MenuCreateUpdateDTO>({ id: 0, name: "", menuItems: [] });
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined);

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [selectedMenuItems, setSelectedMenuItems] = useState<MenuItemDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const menuService = getMenuService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (menuId && menuId > 0) {
            setIsLoading(true);

            menuService
                .getMenu(menuId, eventId)
                .then((response) => {
                    const receivedMenu = response.data as MenuCreateUpdateDTO;
                    setMenu(receivedMenu);
                })
                .catch(() => {
                    setErrorMessage("Beim Laden des Menus ist ein Fehler aufgetreten.");
                })
                .finally(() => {
                    setIsLoading(false);
                });
        }
    }, [id]);

    const onFormInvalid = (data?: FieldValues) => {
        const menuToSave = data as MenuCreateUpdateDTO;
        menuToSave.id = menuId > 0 ? menuId : 0;
        menuToSave.menuItems = selectedMenuItems;

        setMenu(menuToSave);
    };

    const saveMenu = useCallback(
        (data: FieldValues) => {
            setIsSaving(true);

            const menuToSave = data as MenuCreateUpdateDTO;
            menuToSave.id = menuId > 0 ? menuId : undefined;
            menuToSave.menuItems = selectedMenuItems;
            // TODO: calculate price from selected menu items??

            setMenu(menuToSave);

            menuService
                .putMenu(eventId, menuToSave)
                .then((response) => {
                    if (response.status === 201 || response.status === 200) {
                        navigate("../menu");
                    } else {
                        setErrorMessage(`Beim ${menuId > 0 ? "Speichern" : "Erstellen"} des Menus ist ein Fehler aufgetreten.`);
                    }
                })
                .catch(() => {
                    setErrorMessage(`Beim ${menuId > 0 ? "Speichern" : "Erstellen"} des Menus ist ein Fehler aufgetreten.`);
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
                })
                .catch(() => {
                    setErrorMessage("Beim Löschen des Menus ist ein Fehler aufgetreten.");
                })
                .finally(() => {
                    setIsSaving(false);
                });
        }
    }, [id]);

    const menuActions: MenuActions = {
        saveMenu,
        deleteMenu,
        onFormInvalid,
        setSelectedMenuItems
    };

    return { menu, selectedMenuItems, errorMessage, menuActions, isLoading, isSaving };
};
