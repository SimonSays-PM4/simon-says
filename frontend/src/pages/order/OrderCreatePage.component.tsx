import React from "react";
import { useOrderCreatePage } from "./OrderCreatePage.hooks.tsx";
import { Loader } from "../../components/Loader.tsx";
import { useForm } from "react-hook-form";
import { IngredientDTO, MenuDTO, MenuItemDTO, OrderCreateDTO } from "../../gen/api/api.ts";
import { LoadingButton } from "../../components/LoadingButton.tsx";
import { ButtonType } from "../../enums/ButtonType.ts";
import { Button } from "../../components/Button.tsx";
import { classNames } from "../../helpers/ClassNameHelper.ts";
import { FormInput } from "../../components/form/FormInput.tsx";
import { nameof } from "ts-simple-nameof";
import { useNavigate } from "react-router-dom";
import { Disclosure, Tab } from "@headlessui/react";

export const OrderCreatePageComponent: React.FC = () => {
    const { isLoading, isSaving, menuList, selectedMenus, setSelectedMenus, menuItemList, selectedMenuItems, setSelectedMenuItems, orderActions } = useOrderCreatePage();
    const {
        handleSubmit,
        register,
        getValues
    } = useForm();
    const navigate = useNavigate();

    const selectMenu = (menu: MenuDTO) => {
        const isSelected = selectedMenus.some((selectedMenu) => selectedMenu.id === menu.id);
        if (isSelected) {
            setSelectedMenus(selectedMenus.filter((selectedMenu) => selectedMenu.id !== menu.id));
        } else {
            setSelectedMenus([...selectedMenus, menu]);
        }
    };

    const selectMenuItem = (menuItem: MenuItemDTO) => {
        const isSelected = selectedMenuItems.some((selectedMenu) => selectedMenu.id === menuItem.id);
        if (isSelected) {
            setSelectedMenuItems(selectedMenuItems.filter((selectedMenu) => selectedMenu.id !== menuItem.id));
        } else {
            setSelectedMenuItems([...selectedMenuItems, menuItem]);
        }
    };

    const removeIngredientFromMenuItem = (menuItemId: number, ingredient: IngredientDTO) => {
        const selectedMenuItem = selectedMenuItems.find((selectedMenu) => selectedMenu.id === menuItemId);
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
                                        <div className="max-h-[345px] overflow-y-auto overscroll-auto grid w-full rounded-lg border border-primary p-2 mb-12">
                                            {menuList.map((menu: MenuDTO) => {
                                                const isSelected = selectedMenus.some((selectedMenu) => selectedMenu.id === menu.id);

                                                return (
                                                    <div className="flex">
                                                        <Disclosure>
                                                            <Disclosure.Button className="py-2">
                                                                <div
                                                                    key={menu.id}
                                                                    onClick={() => selectMenu(menu)}
                                                                    className={classNames("rounded-[6px] cursor-pointer md:flex mr-2 mb-2 py-[15px] px-[15px] md:py-[20px] md:pl-[40px] md:pr-[24px] min-h-[30px] gap-3 border border-[#000] border-opacity-[.15]",
                                                                        isSelected ? "border-2 border-primary border-opacity-100" : "")}
                                                                >
                                                                    <span className="my-auto text-secondaryfont">
                                                                        x Mal
                                                                    </span>
                                                                    <p className="min-w-fit my-auto text-secondaryfont break-all">
                                                                        {menu.name}: {menu.price.toFixed(2)} CHF
                                                                    </p>
                                                                </div>
                                                            </Disclosure.Button>
                                                            <Disclosure.Panel className="text-gray-500">
                                                                <ul>
                                                                    {menu.menuItems.map((menuItem) => (
                                                                        <li key={menuItem.id} className="text-xs text-secondaryfont">
                                                                            {menuItem.name}
                                                                        </li>
                                                                    ))}
                                                                </ul>
                                                            </Disclosure.Panel>
                                                        </Disclosure>
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
                                        <div className="max-h-[345px] overflow-y-auto overscroll-auto grid rounded-lg border border-primary p-2 mb-12">
                                            {menuItemList.map((menuItem: MenuItemDTO) => {
                                                const isSelected = selectedMenuItems.some((selectedMenu) => selectedMenu.id === menuItem.id);

                                                return (
                                                    <div className="flex">
                                                        <Disclosure>
                                                            <Disclosure.Button className="py-2">
                                                                <div
                                                                    key={menuItem.id}
                                                                    onClick={() => selectMenuItem(menuItem)}
                                                                    className={classNames("rounded-[6px] cursor-pointer md:flex mr-2 mb-2 py-[15px] px-[15px] md:py-[20px] md:pl-[40px] md:pr-[24px] min-h-[30px] gap-3 border border-[#000] border-opacity-[.15]",
                                                                        isSelected ? "border-2 border-primary border-opacity-100" : "")}
                                                                >
                                                                    <span className="my-auto text-secondaryfont">
                                                                        x Mal
                                                                    </span>
                                                                    <p className="min-w-fit my-auto text-secondaryfont break-all">
                                                                        {menuItem.name}: {menuItem.price.toFixed(2)} CHF
                                                                    </p>
                                                                </div>
                                                            </Disclosure.Button>
                                                            <Disclosure.Panel className="text-gray-500">
                                                                <div className="grid">
                                                                    {menuItem.ingredients.map((ingredient) => (
                                                                        <div key={ingredient.id}>
                                                                            <span>{ingredient.name}:  </span>
                                                                            <a className="text-secondaryfont cursor-pointer" onClick={() => removeIngredientFromMenuItem(menuItem.id, ingredient)}>
                                                                                Remove
                                                                            </a>
                                                                        </div>
                                                                    ))}
                                                                </div>
                                                            </Disclosure.Panel>
                                                        </Disclosure>
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

                        <p className="font-bold">Preis: {(selectedMenus.reduce((sum, menu) => sum + menu.price, 0) + (selectedMenuItems.reduce((sum, menuItem) => sum + menuItem.price, 0))).toFixed(2)} CHF</p>

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