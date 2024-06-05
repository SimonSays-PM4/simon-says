import React from "react";

import Select from "react-select";
import { Controller } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { useStationEditPage } from "./StationEditPage.hooks.tsx";
import { Loader } from "../../components/Loader.tsx";
import { FormInput } from "../../components/form/FormInput.tsx";
import { StationCreateUpdateDTO } from "../../gen/api";
import { LoadingButton } from "../../components/LoadingButton.tsx";
import { ButtonType } from "../../enums/ButtonType.ts";
import { Button } from "../../components/Button.tsx";

export const StationEditPageComponent: React.FC = () => {
    const { station, selectedIngredientOptions, stationActions, isLoading, isSaving, ingredientOptions, formErrors, formControl, formRegister, formGetValues, handleSubmit, setIsAssembly, isAssembly } = useStationEditPage();

    const navigate = useNavigate();

    const isUpdating = station.id != undefined && station.id > 0;
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
                    <h2 className="text-xl font-semibold text-default-800 mb-4">{isUpdating ? <>Station <b>"{station.name}"</b> bearbeiten</> : "Station erstellen"}</h2>

                    <form id="stationCreateForm" onSubmit={handleSubmit(() => stationActions.saveStation(formGetValues()), () => stationActions.onFormInvalid(formGetValues()))}>
                        <FormInput id={nameof<StationCreateUpdateDTO>(e => e.name)}
                            label="Name"
                            type="text"
                            register={formRegister}
                            isRequired={true}
                            minLength={1}
                            maxLength={64}
                            defaultValue={station?.name ?? undefined}
                            onChange={(e) => { station.name = e.target.value }}
                            validationError={getErrorMessage(nameof<StationCreateUpdateDTO>(e => e.name))} />


                        <label className="mb-2 block text-sm font-medium text-default-900">
                            Zutaten *
                        </label>

                        <Controller
                            name={nameof<StationCreateUpdateDTO>(e => e.ingredients)}
                            rules={{ required: true, minLength: 1 }}
                            defaultValue={selectedIngredientOptions}
                            control={formControl}
                            render={({ field: { onChange, value, ref } }) => (
                                <Select
                                    id="ingredientSelector"
                                    isMulti
                                    ref={ref}
                                    options={ingredientOptions}
                                    defaultValue={value}
                                    onChange={(values) => {
                                        onChange(values);
                                    }}
                                    className="basic-multi-select"
                                    classNamePrefix="select" />
                            )}
                        />
                        <div className="flex">
                            <div className="sm:grid sm:grid-flow-row sm:grid-cols-1 sm:items-end my-4">
                                <label htmlFor="isAssembly" className="mb-2 block text-sm font-medium text-default-900">
                                    Ist es eine Zusammensetzungs-Station?
                                </label>

                                <div className="mt-1 sm:mt-0 sm:col-span-1 stroke-secondaryfont flex flex-row items-center">
                                    <div className="w-full relative">
                                        <input
                                            id="isAssembly"
                                            className="form-input rounded-lg border border-default-200 px-4 py-2.5 scale-[2] ml-2 mt-3 mb-6"
                                            onChange={(e) => setIsAssembly(e.target.checked)}
                                            checked={isAssembly}
                                            type="checkbox"
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="flex min-h-[60px] items-end ml-auto">
                            <LoadingButton buttonText={isUpdating ? "Speichern" : "Erstellen"} className="my-2" type="submit" buttonType={ButtonType.Primary} isLoading={isSaving} />
                            <Button buttonText="Abbrechen" className="my-2 ml-2" onClick={() => navigate("../station")} buttonType={ButtonType.Secondary} />
                        </div>
                    </form>
                </>)
            }
        </div>
    );
}