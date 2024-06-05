import React from "react";
import { Button } from "../../../components/Button.tsx";
import { FormInput } from "../../../components/form/FormInput.tsx";
import { useNavigate } from "react-router-dom";
import { Loader } from "../../../components/Loader.tsx";
import { ButtonType } from "../../../enums/ButtonType.ts";
import { LoadingButton } from "../../../components/LoadingButton.tsx";
import { useMenuCreatePage } from "./MenuCreatePage.hooks.tsx";
import { MenuCreateUpdateDTO } from "../../../gen/api/api.ts";
import Select from "react-select";
import { Controller } from "react-hook-form";

export const MenuCreatePageComponent: React.FC = () => {
    const { menu, selectedMenuItemOptions, menuActions, isLoading, isSaving, menuItemOptions, formErrors, formControl, formRegister, formGetValues, handleSubmit } = useMenuCreatePage();
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

                    <form id="menuCreateForm" onSubmit={handleSubmit(() => menuActions.saveMenu(formGetValues()), () => menuActions.onFormInvalid(formGetValues()))}>
                        <FormInput id={nameof<MenuCreateUpdateDTO>(e => e.name)}
                            label="Name"
                            type="text"
                            register={formRegister}
                            isRequired={true}
                            minLength={1}
                            maxLength={64}
                            defaultValue={menu?.name ?? undefined}
                            onChange={(e) => { menu.name = e.target.value }}
                            validationError={getErrorMessage(nameof<MenuCreateUpdateDTO>(e => e.name))} />

                        <label className="mb-2 block text-sm font-medium text-default-900">
                            Menüpunkte *
                        </label>

                        <Controller
                            name={nameof<MenuCreateUpdateDTO>(e => e.menuItems)}
                            rules={{ required: true, minLength: 1 }}
                            defaultValue={selectedMenuItemOptions}
                            control={formControl}
                            render={({ field: { onChange, value, ref } }) => (
                                <Select
                                    id={"menuItemSelector"}
                                    isMulti
                                    ref={ref}
                                    options={menuItemOptions}
                                    defaultValue={value}
                                    onChange={(values) => {
                                        onChange(values);
                                    }}
                                    className="basic-multi-select"
                                    classNamePrefix="select" />
                            )}
                        />

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