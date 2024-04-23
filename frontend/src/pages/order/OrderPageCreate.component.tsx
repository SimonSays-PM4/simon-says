import React from "react";
import { useOrderCreatePage } from "./OrderPageCreate.hooks.tsx";
import { Loader } from "../../components/Loader.tsx";
import { useForm } from "react-hook-form";
import { MenuDTO, MenuItemDTO, OrderCreateDTO } from "../../gen/api/api.ts";
import { LoadingButton } from "../../components/LoadingButton.tsx";
import { ButtonType } from "../../enums/ButtonType.ts";
import { Button } from "../../components/Button.tsx";
import { classNames } from "../../helpers/ClassNameHelper.ts";
import { FormInput } from "../../components/form/FormInput.tsx";
import { nameof } from "ts-simple-nameof";

export const OrderCreatePageComponent: React.FC = () => {
    const { isLoading, isSaving, menuList, selectedMenus, setSelectedMenus, menuItemList, selectedMenuItems, setSelectedMenuItems, orderActions } = useOrderCreatePage();
    const {
        handleSubmit,
        register,
        getValues
    } = useForm();

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

                        {menuList.length > 0 ? (
                            <div className="max-h-[345px] overflow-y-auto overscroll-auto grid grid-cols-2 pb-12">
                                {menuList.map((menu: MenuDTO) => {
                                    const isSelected = selectedMenus.some((selectedMenu) => selectedMenu.id === menu.id);

                                    return (
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

                                            <div className="flex ml-auto">
                                                <Button buttonText="+" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={(e) => e.stopPropagation()} disabled={!isSelected} />
                                                <Button buttonText="-" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={(e) => e.stopPropagation()} disabled={!isSelected} />
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <></>
                        )}

                        {menuItemList.length > 0 ? (
                            <div className="max-h-[345px] overflow-y-auto overscroll-auto grid grid-cols-2 pb-12">
                                {menuItemList.map((menuItem: MenuItemDTO) => {
                                    const isSelected = selectedMenuItems.some((selectedMenu) => selectedMenu.id === menuItem.id);

                                    return (
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

                                            <ul>
                                                {menuItem.ingredients.map((ingredient) => (
                                                    <li key={ingredient.id} className="text-xs text-secondaryfont">
                                                        {ingredient.name}
                                                    </li>
                                                ))}
                                            </ul>

                                            <div className="flex ml-auto">
                                                <Button buttonText="+" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={(e) => e.stopPropagation()} disabled={!isSelected} />
                                                <Button buttonText="-" className="my-2 ml-2" buttonType={ButtonType.Secondary} onClick={(e) => e.stopPropagation()} disabled={!isSelected} />
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <></>
                        )}

                        <p className="font-bold">Preis: {(selectedMenus.reduce((sum, menu) => sum + menu.price, 0) + (selectedMenuItems.reduce((sum, menuItem) => sum + menuItem.price, 0))).toFixed(2)} CHF</p>

                        <div className="flex min-h-[60px] items-end ml-auto">
                            <LoadingButton buttonText="Bestellung aufgeben" className="my-2" type="submit" buttonType={ButtonType.Primary} isLoading={isSaving} />
                            <Button buttonText="Abbrechen" className="my-2 ml-2" buttonType={ButtonType.Secondary} />
                        </div>
                    </form>
                </>)
            }
        </div>
    );
}