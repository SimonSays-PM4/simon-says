import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../components/Button";
import { IngredientCreateUpdateDTO } from "../../gen/api";
import { FormInput } from "../../components/form/FormInput";
import { nameof } from "ts-simple-nameof";
import { useIngredientCreatePage } from "./IngredientCreatePage.hooks.tsx";
import { useNavigate } from "react-router-dom";
import { Loader } from "../../components/Loader.tsx";
import { ButtonType } from "../../enums/ButtonType.ts";
import { LoadingButton } from "../../components/LoadingButton.tsx";

export const IngredientCreatePageComponent: React.FC = () => {
    const { errorMessage, ingredient, ingredientActions, isLoading, isSaving } = useIngredientCreatePage();
    const navigate = useNavigate();

    const isUpdating = ingredient.id != undefined && ingredient.id > 0;
    const fieldRequiredMessage = "Dieses Feld ist erforderlich.";

    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
        setValue
    } = useForm();

    const getErrorMessage = (fieldId: string) => {
        if (errors && errors[fieldId] !== undefined) {
            if (errors[fieldId]!.type === "required") {
                return fieldRequiredMessage;
            }

            return undefined;
        }
    };

    return (
        <div>
            {isLoading
                ? (<div className="w-[100px] block mx-auto"><Loader /></div>)
                : (<>
                    <h2 className="text-xl font-semibold text-default-800 mb-4">{isUpdating ? <>Zutat <b>"{ingredient.name}"</b> bearbeiten</> : "Zutate erstellen"}</h2>

                    <form onSubmit={handleSubmit(() => ingredientActions.saveIngredient(getValues()), () => ingredientActions.onFormInvalid(getValues()))}>
                        <FormInput id={nameof<IngredientCreateUpdateDTO>(e => e.name)}
                            defaultValue={ingredient.name}
                            label={"Name"}
                            type="text"
                            register={register}
                            isRequired={true}
                            validationError={getErrorMessage(nameof<IngredientCreateUpdateDTO>(e => e.name))} />

                        <div className="sm:grid sm:grid-flow-row sm:grid-cols-1 sm:items-end my-4">
                            <label htmlFor={nameof<IngredientCreateUpdateDTO>(e => e.mustBeProduced)} className="mb-2 block text-sm font-medium text-default-900">
                                Muss produziert werden
                            </label>

                            <div className="mt-1 sm:mt-0 sm:col-span-1 stroke-secondaryfont flex flex-row items-center">
                                <div className="w-full relative">
                                    <input
                                        id={nameof<IngredientCreateUpdateDTO>(e => e.mustBeProduced)}
                                        className="form-input rounded-lg border border-default-200 px-4 py-2.5 scale-[2] ml-2 mt-3 mb-6"
                                        onChange={(e) => setValue(nameof<IngredientCreateUpdateDTO>(e => e.mustBeProduced), e.target.checked)}
                                        type="checkbox"
                                    />
                                </div>
                            </div>
                        </div>

                        {errorMessage ? <p className="py-2 text-primary">{errorMessage}</p> : <></>}
                        <div className="flex min-h-[60px] items-end ml-auto">
                            <LoadingButton buttonText={isUpdating ? "Speichern" : "Erstellen"} className="my-2" type="submit" buttonType={ButtonType.Primary} isLoading={isSaving} />
                            <Button buttonText="Abbrechen" className="my-2 ml-2" onClick={() => navigate("../ingredients")} buttonType={ButtonType.Secondary} />
                        </div>
                    </form>
                </>)
            }
        </div>
    );
}