import React from "react";
import { useOrderCreatePage } from "./OrderCreatePage.hooks.tsx";
import { Loader } from "../../components/Loader.tsx";
import { useForm } from "react-hook-form";
import { IngredientDTO, OrderCreateDTO, OrderMenuDTO, OrderMenuItemDTO } from "../../gen/api/api.ts";
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

export const OrderCreatePageComponent: React.FC = () => {
    const { isLoading, isSaving, menuList, selectedMenus, setSelectedMenus, menuItemList, selectedMenuItems, setSelectedMenuItems, orderActions } = useOrderCreatePage();

    const menuIndex = React.useRef<number>(0);
    const menuItemIndex = React.useRef<number>(0);

    const {
        handleSubmit,
        register,
        getValues
    } = useForm();
    const navigate = useNavigate();

    const selectMenu = (menu: OrderMenuDTO) => {
        setSelectedMenus([...selectedMenus, new OrderMenuModel(menuIndex.current + 1, menu)]);
        menuIndex.current = menuIndex.current + 1;
    };

    const selectMenuItem = (menuItem: OrderMenuItemDTO) => {
        setSelectedMenuItems([...selectedMenuItems, new OrderMenuItemModel(menuItemIndex.current + 1, menuItem)]);
        menuItemIndex.current = menuItemIndex.current + 1;
    };

    const removeIngredientFromMenuItem = (menuItemIndex: number, ingredient: IngredientDTO) => {
        const selectedMenuItem = selectedMenuItems.find((selectedMenu) => selectedMenu.index === menuItemIndex);
        if (selectedMenuItem && selectedMenuItem.ingredients.length > 1) { // can not delete last ingredients
            selectedMenuItem.ingredients = selectedMenuItem.ingredients.filter((selectedIngredient) => selectedIngredient.id !== ingredient.id);
            setSelectedMenuItems([...selectedMenuItems]);
        }
    };

    return (
        <div className="w-full">
            {isLoading
                ? (<div className="w-[100px] block mx-auto"><Loader /></div>)
                : (<>
                    <h2 className="text-xl font-semibold text-default-800 mb-4">Bestellung</h2>

                    <form onSubmit={handleSubmit((data) => orderActions.saveOrder(data), () => orderActions.onFormInvalid(getValues()))}>
                        <FormInput id={nameof<OrderCreateDTO>(e => e.tableNumber)}
                            label={"Tischnummer"}
                            type="number"
                            register={register}
                            isRequired={true}
                        />

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
                                            {menuList.map((menu: OrderMenuDTO) => {
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
                                            {menuItemList.map((menuItem: OrderMenuItemDTO) => {
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

                        <div className="grid w-full border-t-2 mb-6">
                            <p>Selektierte Menus</p>
                            {selectedMenus.map((menu) => {
                                return (
                                    <div key={menu.id} className="w-full">
                                        <p className="font-bold">{menu.name}: {menu.price.toFixed(2)} CHF</p>
                                    </div>
                                );
                            })}
                        </div>

                        <div className="grid w-full border-t-2 mb-12">
                            <p>Selektierte Menu Items</p>
                            {selectedMenuItems.map((menuItem) => {
                                return (
                                    <div key={menuItem.index} className="w-full">
                                        <p className="font-bold">{menuItem.name}: {menuItem.price.toFixed(2)} CHF</p>

                                        {menuItem.ingredients.length > 0 ? (
                                            <div className="grid">
                                                {menuItem.ingredients.map((ingredient: OrderIngredientModel) => (
                                                    <div key={ingredient.id}>
                                                        <span>{ingredient.name}</span>

                                                        {menuItem.ingredients.length > 1
                                                            ? (
                                                                <a className="text-primary cursor-pointer ml-4" onClick={() => removeIngredientFromMenuItem(menuItem.index, ingredient)}>
                                                                    {"<- Entfernen"}
                                                                </a>
                                                            )
                                                            : (<></>)
                                                        }
                                                    </div>
                                                ))}
                                            </div>)
                                            : (<></>)
                                        }
                                    </div>
                                );
                            })}
                        </div>

                        <div className="w-full border-t-2">
                            <p className="font-bold">Preis: {(selectedMenus.reduce((sum, menu) => sum + menu.price, 0) + (selectedMenuItems.reduce((sum, menuItem) => sum + menuItem.price, 0))).toFixed(2)} CHF</p>
                        </div>

                        <div className="flex min-h-[60px] items-end ml-auto">
                            <LoadingButton buttonText="Bestellung aufgeben" className="my-2" type="submit" buttonType={ButtonType.Primary} isLoading={isSaving} />
                            <Button buttonText="Abbrechen" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={() => navigate("../")} />
                        </div>
                    </form>
                </>)
            }
        </div>
    );
}