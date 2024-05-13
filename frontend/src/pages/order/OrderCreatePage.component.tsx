import React, { useRef, useState } from "react";
import { useOrderCreatePage } from "./OrderCreatePage.hooks.tsx";
import { Loader } from "../../components/Loader.tsx";
import { useForm } from "react-hook-form";
import { MenuDTO, MenuItemDTO, OrderCreateDTO } from "../../gen/api/api.ts";
import { LoadingButton } from "../../components/LoadingButton.tsx";
import { ButtonType } from "../../enums/ButtonType.ts";
import { Button } from "../../components/Button.tsx";
import { classNames } from "../../helpers/ClassNameHelper.ts";
import { FormInput } from "../../components/form/FormInput.tsx";
import { nameof } from "ts-simple-nameof";
import { useNavigate } from "react-router-dom";
import { Tab } from "@headlessui/react";
import { OrderMenuModel } from "../../models/OrderMenuModel.ts";
import { OrderMenuItemModel } from "../../models/OrderMenuItemModel.ts";
import { OrderIngredientModel } from "../../models/OrderIngredientModel.ts";
import { Dialog } from "../../components/Dialog.tsx";

export const OrderCreatePageComponent: React.FC = () => {
    const { isLoading, isSaving, errorMessage, menuList, selectedMenus, setSelectedMenus, menuItemList, selectedMenuItems, setSelectedMenuItems, orderActions, isTakeAway, setIsTakeAway } = useOrderCreatePage();

    const menuIndex = useRef<number>(0);
    const menuItemIndex = useRef<number>(0);

    const [isEditMenuItemDialogOpen, setIsEditMenuItemDialogOpen] = useState<boolean>(false);
    const [menuItemToEdit, setMenuItemToEdit] = useState<OrderMenuItemModel | undefined>(undefined);

    const [isEditMenuDialogOpen, setIsEditMenuDialogOpen] = useState<boolean>(false);
    const [menuToEdit, setMenuToEdit] = useState<OrderMenuModel | undefined>(undefined);

    const {
        handleSubmit,
        register,
        setValue
    } = useForm();
    const navigate = useNavigate();

    const selectMenu = (menu: MenuDTO) => {
        setSelectedMenus([...selectedMenus, new OrderMenuModel(menuIndex.current + 1, menu)]);
        menuIndex.current = menuIndex.current + 1;
    };

    const selectMenuItem = (menuItem: MenuItemDTO) => {
        setSelectedMenuItems([...selectedMenuItems, new OrderMenuItemModel(menuItemIndex.current + 1, menuItem)]);
        menuItemIndex.current = menuItemIndex.current + 1;
    };

    const removeMenu = (menuIndex: number) => {
        setSelectedMenus(selectedMenus.filter((selectedMenu) => selectedMenu.index !== menuIndex));
    }

    const removeMenuItem = (menuItemIndex: number) => {
        setSelectedMenuItems(selectedMenuItems.filter((selectedMenuItem) => selectedMenuItem.index !== menuItemIndex));
    }

    const removeMenuItemFromMenu = (menuIndex: number, menuItem: OrderMenuItemModel) => {
        const selectedMenu = selectedMenus.find((selectedMenu) => selectedMenu.index === menuIndex);
        if (selectedMenu && selectedMenu.menuItems.length > 1) { // can not delete last menu item
            selectedMenu.menuItems = selectedMenu.menuItems.filter((selectedMenuItem) => selectedMenuItem.id !== menuItem.id);
            setSelectedMenus([...selectedMenus]);
        }
    }

    const removeIngredientFromMenuItemInMenu = (menuIndex: number, menuItemIndex: number, ingredient: OrderIngredientModel) => {
        const selectedMenu = selectedMenus.find((selectedMenu) => selectedMenu.index === menuIndex);
        if (selectedMenu) {
            const selectedMenuItem = selectedMenu.menuItems.find((selectedMenuItem) => selectedMenuItem.index === menuItemIndex);
            if (selectedMenuItem && selectedMenuItem.ingredients.length > 1) { // can not delete last ingredients
                selectedMenuItem.ingredients = selectedMenuItem.ingredients.filter((selectedIngredient) => selectedIngredient.id !== ingredient.id);
                setSelectedMenus([...selectedMenus]);
            }
        }
    };

    const removeIngredientFromMenuItem = (menuItemIndex: number, ingredient: OrderIngredientModel) => {
        const selectedMenuItem = selectedMenuItems.find((selectedMenu) => selectedMenu.index === menuItemIndex);
        if (selectedMenuItem && selectedMenuItem.ingredients.length > 1) { // can not delete last ingredients
            selectedMenuItem.ingredients = selectedMenuItem.ingredients.filter((selectedIngredient) => selectedIngredient.id !== ingredient.id);
            setSelectedMenuItems([...selectedMenuItems]);
        }
    };

    const updateMenuItem = (updatedMenuItem?: OrderMenuItemModel) => {
        if (updatedMenuItem) {
            const index = selectedMenuItems.findIndex((selectedMenu) => selectedMenu.index === updatedMenuItem.index);
            if (index !== -1) {
                selectedMenuItems[index] = updatedMenuItem;
                setSelectedMenuItems([...selectedMenuItems]);
            }
        }
    }

    const updateMenu = (updatedMenu?: OrderMenuModel) => {
        if (updatedMenu) {
            const index = selectedMenus.findIndex((selectedMenu) => selectedMenu.index === updatedMenu.index);
            if (index !== -1) {
                selectedMenus[index] = updatedMenu;
                setSelectedMenus([...selectedMenus]);
            }
        }
    }

    const onIsTakeAwayChanged = (isTakeAway: boolean) => {
        setValue(nameof<OrderCreateDTO>(e => e.tableNumber), undefined);
        setIsTakeAway(isTakeAway);
    }

    return (
        <div className="w-full">
            {isLoading
                ? (<div className="w-[100px] block mx-auto"><Loader /></div>)
                : (<>
                    <h2 className="text-xl font-semibold text-default-800 mb-4">Bestellung</h2>

                    <form onSubmit={handleSubmit((data) => orderActions.saveOrder(data))}>
                        <div className="flex">
                            <div className="sm:grid sm:grid-flow-row sm:grid-cols-1 sm:items-end my-4">
                                <label htmlFor="isTakeAway" className="mb-2 block text-sm font-medium text-default-900">
                                    Takeaway
                                </label>

                                <div className="mt-1 sm:mt-0 sm:col-span-1 stroke-secondaryfont flex flex-row items-center">
                                    <div className="w-full relative">
                                        <input
                                            id="isTakeAway"
                                            className="form-input rounded-lg border border-default-200 px-4 py-2.5 scale-[2] ml-2 mt-3 mb-6"
                                            onChange={(e) => onIsTakeAwayChanged(e.target.checked)}
                                            type="checkbox"
                                        />
                                    </div>
                                </div>
                            </div>

                            <FormInput id={nameof<OrderCreateDTO>(e => e.tableNumber)}
                                label="Tischnummer"
                                type="number"
                                register={register}
                                classNames="ml-12"
                                disabled={isTakeAway}
                            />
                        </div>

                        {errorMessage && <p className="text-red-500 pb-4">{errorMessage}</p>}

                        <Tab.Group>
                            <Tab.List className="flex space-x-1 rounded-xl p-1">
                                <Tab
                                    className={({ selected }) =>
                                        classNames(
                                            "w-full rounded-lg px-1 py-2 sm:px-3 sm:py-4",
                                            selected
                                                ? "bg-primary text-white"
                                                : "border-2 border-primary"
                                        )
                                    }
                                >
                                    Menus
                                </Tab>
                                <Tab
                                    className={({ selected }) =>
                                        classNames(
                                            "w-full rounded-lg px-1 py-2 sm:px-3 sm:py-4",
                                            selected
                                                ? "bg-primary text-white"
                                                : "border-2 border-primary"
                                        )
                                    }
                                >
                                    Menu Items
                                </Tab>
                            </Tab.List>
                            <Tab.Panels>
                                <Tab.Panel>
                                    {menuList.length > 0 ? (
                                        <div className="max-h-[345px] overflow-y-auto overscroll-auto grid grid-cols-2 w-full rounded-lg p-2 mb-12">
                                            {menuList.map((menu: MenuDTO) => {
                                                return (
                                                    <div
                                                        key={menu.id}
                                                        onClick={() => selectMenu(menu)}
                                                        className={"rounded-[6px] cursor-pointer md:flex mr-2 mb-2 py-[15px] px-[15px] md:py-[20px] md:pl-[40px] md:pr-[24px] min-h-[30px] gap-3 border border-[#000] border-opacity-[.15]"}
                                                    >
                                                        <p className="min-w-fit my-auto text-secondaryfont break-all">
                                                            {menu.name}: {menu.price.toFixed(2)} CHF
                                                        </p>
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    ) : (
                                        <></>
                                    )}
                                </Tab.Panel>
                                <Tab.Panel>
                                    {menuItemList.length > 0 ? (
                                        <div className="max-h-[345px] overflow-y-auto overscroll-auto grid grid-cols-2 rounded-lg p-2 mb-12">
                                            {menuItemList.map((menuItem: MenuItemDTO) => {
                                                return (
                                                    <div
                                                        key={menuItem.id}
                                                        onClick={() => selectMenuItem(menuItem)}
                                                        className={"rounded-[6px] cursor-pointer md:flex mr-2 mb-2 py-[15px] px-[15px] md:py-[20px] md:pl-[40px] md:pr-[24px] min-h-[30px] gap-3 border border-[#000] border-opacity-[.15]"}
                                                    >
                                                        <p className="min-w-fit my-auto text-secondaryfont break-all">
                                                            {menuItem.name}: {menuItem.price.toFixed(2)} CHF
                                                        </p>
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    ) : (
                                        <></>
                                    )}
                                </Tab.Panel>
                            </Tab.Panels>
                        </Tab.Group>

                        <div className="grid w-full border-2 rounded-lg p-2 mb-6">
                            <p>Selektierte Menus</p>
                            {selectedMenus.map((menu) => {
                                return (
                                    <div key={menu.index + menu.id} className="w-full flex my-2 border-t-2 pt-2">
                                        <div>
                                            <p className="font-bold">{menu.name}: {menu.price.toFixed(2)} CHF</p>

                                            {menu.menuItems.length > 0 ? (
                                                <div className="grid">
                                                    {menu.menuItems.map((menuItem: OrderMenuItemModel) => (
                                                        <div key={menu.index + menuItem.id}>
                                                            <span>{menuItem.name} - ({menuItem.ingredients.map((i) => i.name).join(", ")})</span>
                                                        </div>
                                                    ))}
                                                </div>)
                                                : (<></>)
                                            }
                                        </div>
                                        <div className="flex my-auto grow justify-end">
                                            <Button buttonText="Bearbeiten"
                                                className="my-2"
                                                buttonType={ButtonType.Primary}
                                                disabled={menu.menuItems.length <= 1 && menu.menuItems[0].ingredients.length <= 1}
                                                onClick={() => { setMenuToEdit(menu); setIsEditMenuDialogOpen(true); }} />
                                            <Button buttonText="Entfernen" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={() => removeMenu(menu.index)} />
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        <div className="grid w-full border-2 rounded-lg p-2 mb-12">
                            <p>Selektierte Menu Items</p>
                            {selectedMenuItems.map((menuItem) => {
                                return (
                                    <div key={menuItem.id + menuItem.index} className="w-full flex my-2 border-t-2 pt-2">
                                        <div>
                                            <p className="font-bold">{menuItem.name}: {menuItem.price.toFixed(2)} CHF</p>
                                            <span>{menuItem.name} - ({menuItem.ingredients.map((i) => i.name).join(", ")})</span>
                                        </div>

                                        <div className="flex my-auto grow justify-end">
                                            <Button buttonText="Bearbeiten"
                                                className="my-2"
                                                buttonType={ButtonType.Primary}
                                                disabled={menuItem.ingredients.length <= 1}
                                                onClick={() => { setMenuItemToEdit(menuItem); setIsEditMenuItemDialogOpen(true); }} />
                                            <Button buttonText="Entfernen" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={() => removeMenuItem(menuItem.index)} />
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        <div className="w-full border-t-2">
                            <p className="font-bold">{`Preis: ${(selectedMenus.reduce((sum, menu) => sum + menu.price, 0) + (selectedMenuItems.reduce((sum, menuItem) => sum + menuItem.price, 0))).toFixed(2)} CHF`}</p>
                        </div>

                        <div className="flex min-h-[60px] items-end ml-auto">
                            <LoadingButton buttonText="Bestellung aufgeben" className="my-2" type="submit" buttonType={ButtonType.Primary} isLoading={isSaving} />
                            <Button buttonText="Abbrechen" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={() => navigate("../")} />
                        </div>
                    </form>
                </>)
            }

            <Dialog title={`Menu "${menuToEdit?.name}" bearbeiten`}
                isOpen={isEditMenuDialogOpen}
                cancelAction={() => setIsEditMenuDialogOpen(false)}
                cancelText="Abbrechen"
                okAction={() => { updateMenu(menuToEdit); setIsEditMenuDialogOpen(false); }}
                okText="Speichern"
                setIsOpen={setIsEditMenuDialogOpen}>
                <>
                    {menuToEdit && menuToEdit.menuItems.length > 0 ? (
                        <div className="grid my-2">
                            {menuToEdit.menuItems.map((menuItem: OrderMenuItemModel) => (
                                <div key={menuToEdit.index + menuItem.id} className="rounded-lg border-2 mb-4">
                                    <div className="grid grid-cols-2 border-b-2 pb-2 mb-2 pl-3">
                                        <p className="my-auto">{menuItem.name}</p>

                                        {menuToEdit.menuItems.length > 1
                                            ? (
                                                <Button buttonText="Entfernen" className="my-2 ml-auto mr-2" buttonType={ButtonType.Secondary} onClick={() => removeMenuItemFromMenu(menuToEdit.index, menuItem)} />
                                            )
                                            : (<></>)
                                        }
                                    </div>

                                    <div className="grid ml-6">
                                        {menuItem.ingredients.map((ingredient: OrderIngredientModel, index) => (
                                            <div key={menuItem.index + ingredient.id} className={`grid grid-cols-2 ${index === menuItem.ingredients.length - 1 ? " my-2" : "border-b-2"}`}>
                                                <p className="my-auto">{ingredient.name}</p>

                                                {menuItem.ingredients.length > 1
                                                    ? (
                                                        <Button buttonText="Entfernen" className="my-2 ml-auto mr-2" buttonType={ButtonType.Secondary} onClick={() => removeIngredientFromMenuItemInMenu(menuToEdit.index, menuItem.index, ingredient)} />
                                                    )
                                                    : (<></>)
                                                }
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>)
                        : (<p></p>)
                    }
                </>
            </Dialog>

            <Dialog title={`Menu Item "${menuItemToEdit?.name}" bearbeiten`}
                isOpen={isEditMenuItemDialogOpen}
                cancelAction={() => setIsEditMenuItemDialogOpen(false)}
                cancelText="Abbrechen"
                okAction={() => { updateMenuItem(menuItemToEdit); setIsEditMenuItemDialogOpen(false); }}
                okText="Speichern"
                setIsOpen={setIsEditMenuItemDialogOpen}>
                <>
                    {menuItemToEdit && menuItemToEdit.ingredients.length > 0 ? (
                        <div className="grid my-2">
                            {menuItemToEdit.ingredients.map((ingredient: OrderIngredientModel, index) => (
                                <div key={menuItemToEdit.index + ingredient.id} className={`grid grid-cols-2 ${index === menuItemToEdit.ingredients.length - 1 ? " my-2" : "border-b-2"}`}>
                                    <p className="my-auto">{ingredient.name}</p>

                                    {menuItemToEdit.ingredients.length > 1
                                        ? (
                                            <Button buttonText="Entfernen" className="my-2 ml-auto mr-2" buttonType={ButtonType.Secondary} onClick={() => removeIngredientFromMenuItem(menuItemToEdit.index, ingredient)} />
                                        )
                                        : (<></>)
                                    }
                                </div>
                            ))}
                        </div>)
                        : (<p></p>)
                    }
                </>
            </Dialog>
        </div >
    );
}