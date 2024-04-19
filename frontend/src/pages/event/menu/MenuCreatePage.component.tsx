import React from "react";
import { Button } from "../../../components/Button.tsx";
import { FormInput } from "../../../components/form/FormInput.tsx";
import { nameof } from "ts-simple-nameof";
import { useNavigate } from "react-router-dom";
import { Loader } from "../../../components/Loader.tsx";
import { ButtonType } from "../../../enums/ButtonType.ts";
import { LoadingButton } from "../../../components/LoadingButton.tsx";
import { useMenuCreatePage } from "./MenuCreatePage.hooks.tsx";
import { MenuCreateUpdateDTO } from "../../../gen/api/api.ts";
import Select from "react-select";

export const MenuCreatePageComponent: React.FC = () => {
    const { menu, selectedMenuItemOptions, setSelectedMenuItemOptions, menuActions, isLoading, isSaving, menuItemOptions, formErrors, formRegister, formGetValues, handleSubmit, setValue } = useMenuCreatePage();
    const navigate = useNavigate();

    const isUpdating = menu.id != undefined && menu.id > 0;
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
    const fieldLengthMessage = "Die Eingabe ist muss zwischen 1 und 64 Zeichen sein.";

    const getErrorMessage = (fieldId: string) => {
        if (formErrors && formErrors[fieldId] !== undefined) {
            if (formErrors[fieldId]!.type === "required") {
                return fieldRequiredMessage;
            }
            if (formErrors[fieldId]!.type === "minLength" || formErrors[fieldId]!.type === "maxLength") {
                return fieldLengthMessage;
            }

            return undefined;
        }
    };

    return (
        <div>
            {isLoading
                ? (<div className="w-[100px] block mx-auto"><Loader /></div>)
                : (<>
                    <h2 className="text-xl font-semibold text-default-800 mb-4">{isUpdating ? <>Menu <b>"{menu.name}"</b> bearbeiten</> : "Menu erstellen"}</h2>

                    <form id="menuCreateForm" onSubmit={handleSubmit((data) => menuActions.saveMenu(data), () => menuActions.onFormInvalid(formGetValues()))}>
                        <FormInput id={nameof<MenuCreateUpdateDTO>(e => e.name)}
                            label="Name"
                            type="text"
                            register={formRegister}
                            isRequired={true}
                            minLength={1}
                            maxLength={64}
                            defaultValue={menu?.name ?? undefined}
                            onChange={(e) => setValue("name", e.target.value)}
                            validationError={getErrorMessage(nameof<MenuCreateUpdateDTO>(e => e.name))} />

                        <label className="mb-2 block text-sm font-medium text-default-900">
                            Menu Items *
                        </label>
                        <Select
                            isMulti
                            name="menuItems"
                            id="menuItems"
                            options={menuItemOptions}
                            value={selectedMenuItemOptions}
                            onChange={(values) => {
                                setValue("menuItems", Array.from(values));
                                setSelectedMenuItemOptions(() => Array.from(values));
                            }}
                            className="basic-multi-select"
                            classNamePrefix="select" />

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