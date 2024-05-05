import React from "react";
import { MenuItemCreateUpdateDTO } from "../../../gen/api";
import { FormInput } from "../../../components/form/FormInput.tsx";
import { useMenuItemEditPage } from "./MenuItemEditPage.hooks.tsx";
import { Controller } from "react-hook-form";
import { Button } from "../../../components/Button.tsx";
import { Popup } from "../../../components/Popup.tsx";
import Select from "react-select";
import { ButtonType } from "../../../enums/ButtonType.ts";
import { Loader } from "../../../components/Loader.tsx";

export const MenuItemEditPage: React.FC = () => {
  const { menuItem, menuItemActions, setShowDeleteModal, showDeleteModal, navigate, ingredientOptions, selectedIngredients, isLoading, formControl, formGetValues, formErrors, formRegister, handleSubmit } = useMenuItemEditPage();

  const { menuItem, menuItemActions, setShowDeleteModal, showDeleteModal, navigate, ingredientOptions, selectedIngredients } = useMenuItemEditPage();
  const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
  const fieldLengthMessage = "Die Eingabe ist muss zwischen 5 und 64 Zeichen sein.";
  const fieldValueMessage = "Die Eingabe ist muss über 1.00 sein.";

  const getErrorMessage = (fieldId: string) => {
    if (formErrors && formErrors[fieldId] !== undefined) {
      if (formErrors[fieldId]!.type === "required") {
        return fieldRequiredMessage;
      }
      if (formErrors[fieldId]!.type === "minLength" || formErrors[fieldId]!.type === "maxLength") {
        return fieldLengthMessage;
      }
      if (formErrors[fieldId]!.type === "min" || formErrors[fieldId]!.type === "max") {
        return fieldValueMessage;
      }

      return undefined;
    }
  };

  return (<div>
    {isLoading
      ? (<div className="w-[100px] block mx-auto"><Loader /></div>)
      : (<>
        <h2 className="text-xl font-semibold text-default-800 mb-4">{menuItem.id && menuItem.id > 0 ? "Edit Menu Item" : "Menu Item erstellen"}</h2>
        <form onSubmit={handleSubmit(() => menuItemActions.saveMenuItem(formGetValues()), () => menuItemActions.onFormInvalid(formGetValues()))} >
          <FormInput id={nameof<MenuItemCreateUpdateDTO>(e => e.name)}
            defaultValue={menuItem.name}
            label={"Name"}
            type="text"
            register={formRegister}
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
            register={formRegister}
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
            control={formControl}
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
      </>)}
  </div>)
}