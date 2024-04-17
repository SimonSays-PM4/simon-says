import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../../components/Button.tsx";
import { FormInput } from "../../../components/form/FormInput.tsx";
import { nameof } from "ts-simple-nameof";
import { useNavigate } from "react-router-dom";
import { Loader } from "../../../components/Loader.tsx";
import { ButtonType } from "../../../enums/ButtonType.ts";
import { LoadingButton } from "../../../components/LoadingButton.tsx";
import { useMenuCreatePage } from "./MenuCreatePage.hooks.tsx";
import { MenuCreateUpdateDTO, MenuItemDTO } from "../../../gen/api/api.ts";
import { classNames } from "../../../helpers/ClassNameHelper.ts";

export const MenuCreatePageComponent: React.FC = () => {
    const { errorMessage, menu, selectedMenuItems, menuActions, isLoading, isSaving } = useMenuCreatePage();
    const navigate = useNavigate();

    const isUpdating = menu.id != undefined && menu.id > 0;
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
    const fieldLengthMessage = "Die Eingabe ist muss zwischen 1 und 64 Zeichen sein.";

    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
    } = useForm();

    const getErrorMessage = (fieldId: string) => {
        if (errors && errors[fieldId] !== undefined) {
            if (errors[fieldId]!.type === "required") {
                return fieldRequiredMessage;
            }
            if (errors[fieldId]!.type === "minLength" || errors[fieldId]!.type === "maxLength") {
                return fieldLengthMessage;
            }

            return undefined;
        }
    };

    const selectMenuItem = (menuItem: MenuItemDTO) => {
        const isSelected = selectedMenuItems.some((selectedMenuItem) => selectedMenuItem.id === menuItem.id);
        if (isSelected) {
            menuActions.setSelectedMenuItems(selectedMenuItems.filter((selectedMenuItem) => selectedMenuItem.id !== menuItem.id));
        } else {
            menuActions.setSelectedMenuItems([...selectedMenuItems, menuItem]);
        }
    };

    const dummyMenuItems: MenuItemDTO[] = [
        { id: 1, name: "Test1", price: 10.0, ingredients: [] },
        { id: 2, name: "Test2", price: 20.0, ingredients: [] },
        { id: 3, name: "Test3", price: 30.0, ingredients: [] },
        { id: 4, name: "Test4", price: 40.0, ingredients: [] },
        { id: 5, name: "Test5", price: 50.0, ingredients: [] },
    ];

    return (
        <div>
            {isLoading
                ? (<div className="w-[100px] block mx-auto"><Loader /></div>)
                : (<>
                    <h2 className="text-xl font-semibold text-default-800 mb-4">{isUpdating ? <>Menu <b>"{menu.name}"</b> bearbeiten</> : "Menu erstellen"}</h2>

                    <form onSubmit={handleSubmit(() => menuActions.saveMenu(getValues()), () => menuActions.onFormInvalid(getValues()))}>
                        <FormInput id={nameof<MenuCreateUpdateDTO>(e => e.name)}
                            defaultValue={menu.name}
                            label={"Name"}
                            type="text"
                            register={register}
                            isRequired={true}
                            minLength={1}
                            maxLength={64}
                            validationError={getErrorMessage(nameof<MenuCreateUpdateDTO>(e => e.name))} />

                        {dummyMenuItems.length > 0 ? (
                            <div className="max-h-[345px] overflow-y-auto overscroll-auto">
                                {dummyMenuItems.map((menuItem: MenuItemDTO) => {
                                    const isSelected = selectedMenuItems.some((selectedMenuItem) => selectedMenuItem.id === menuItem.id);
                                    return (
                                        <div
                                            key={menuItem.id}
                                            onClick={() => selectMenuItem(menuItem)}
                                            className={classNames("w-full rounded-[6px] cursor-pointer md:grid md:grid-flow-col mb-2 py-[15px] px-[15px] md:py-[20px] md:pl-[40px] md:pr-[24px] min-h-[30px] gap-3 border border-[#000] border-opacity-[.15]",
                                                isSelected ? "border-primary border-opacity-100" : "")}

                                        >
                                            <p className="my-auto min-w-fit text-secondaryfont break-all">
                                                {menuItem.name} - {menuItem.price.toFixed(2)} CHF
                                            </p>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <></>
                        )}

                        {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}
                        <div className="flex min-h-[60px] items-end ml-auto">
                            <LoadingButton buttonText={isUpdating ? "Speichern" : "Erstellen"} className="my-2" type="submit" buttonType={ButtonType.Primary} isLoading={isSaving} />
                            <Button buttonText="Abbrechen" className="my-2 ml-2" onClick={() => navigate("../menu")} buttonType={ButtonType.Secondary} />
                        </div>
                    </form>
                </>)
            }
        </div>
    );
}