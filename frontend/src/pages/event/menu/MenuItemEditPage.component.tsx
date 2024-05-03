import React from "react";
import { MenuItemCreateUpdateDTO } from "../../../gen/api";
import { FormInput } from "../../../components/form/FormInput.tsx";
import { useMenuItemEditPage } from "./MenuItemEditPage.hooks.tsx";
import { Controller, useForm } from "react-hook-form";
import { Button } from "../../../components/Button.tsx";
import { Popup } from "../../../components/Popup.tsx";
import Select from "react-select";
import { ButtonType } from "../../../enums/ButtonType.ts";

export const MenuItemEditPage: React.FC = () => {

  const { menuItem, menuItemActions, setShowDeleteModal, showDeleteModal, navigate, ingredientOptions, selectedIngredients } = useMenuItemEditPage();
  const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
  const fieldLengthMessage = "Die Eingabe ist muss zwischen 5 und 64 Zeichen sein.";
  const fieldValueMessage = "Die Eingabe ist muss über 1.00 sein.";

  const {
    register,
    handleSubmit,
    formState: { errors },
    getValues,
    control
  } = useForm();

  const getErrorMessage = (fieldId: string) => {
    if (errors && errors[fieldId] !== undefined) {
      if (errors[fieldId]!.type === "required") {
        return fieldRequiredMessage;
      }
      if (errors[fieldId]!.type === "minLength" || errors[fieldId]!.type === "maxLength") {
        return fieldLengthMessage;
      }
      if (errors[fieldId]!.type === "min" || errors[fieldId]!.type === "max") {
        return fieldValueMessage;
      }

      return undefined;
    }
  };



  return (<div>
    <h2 className="text-xl font-semibold text-default-800 mb-4">{menuItem.id && menuItem.id > 0 ? "Edit Menu Item" : "Menu Item erstellen"}</h2>
    <form onSubmit={handleSubmit(() => menuItemActions.saveMenuItem(getValues()), () => menuItemActions.onFormInvalid(getValues()))} >
      <FormInput id={nameof<MenuItemCreateUpdateDTO>(e => e.name)}
        defaultValue={menuItem.name}
        label={"Name"}
        type="text"
        register={register}
        isRequired={true}
        minLength={5}
        maxLength={64}
        validationError={getErrorMessage(nameof<MenuItemCreateUpdateDTO>(e => e.name))} />
      <FormInput id={nameof<MenuItemCreateUpdateDTO>(e => e.price)}
        defaultValue={"" + menuItem.price}
        label={"Price"}
        type="number"
        step={0.01}
        min={1}
        register={register}
        isRequired={true}
        validationError={getErrorMessage(nameof<MenuItemCreateUpdateDTO>(e => e.price))}
      />
      <label className="mb-2 block text-sm font-medium text-default-900">
        Ingredients *
      </label>
      <Controller
        name={nameof<MenuItemCreateUpdateDTO>(e => e.ingredients)}
        rules={{ required: true, minLength: 1 }}
        defaultValue={selectedIngredients}
        control={control}
        render={({ field: { onChange, value, ref } }) => (
          <Select
            id={"ingredientSelector"}
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
      <div className="flex min-h-[60px] items-end ml-auto">

        <br />
        <Button buttonText={menuItem.id != undefined && menuItem.id > 0 ? "Speichern" : "Erstellen"} className="my-2" type="submit" />
        {menuItem.id != undefined && menuItem.id > 0 && <Button buttonText="Löschen" className="my-2 mx-2" onClick={() => setShowDeleteModal(true)} />}<Button className="my-2 mx-2" buttonType={ButtonType.Secondary} buttonText={"Zurück"} onClick={() => navigate("./../")} />
      </div>

    </form>
    <Popup show={showDeleteModal} onClose={() => setShowDeleteModal(false)} onAccept={menuItemActions.deleteMenuItem} modalText={"Do you want to delete this menu item?"} acceptText={"Yes"} closeText={"No"} />
  </div>)
}