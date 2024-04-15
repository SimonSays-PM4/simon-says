import React from "react";
import {nameof} from "ts-simple-nameof";
import {MenuItemDTO} from "../../../gen/api";
import {FormInput} from "../../../components/form/FormInput.tsx";
import {useEventMenuEditPage} from "./EventMenuEditPage.hooks.tsx";
import {useForm} from "react-hook-form";
import {Button} from "../../../components/Button.tsx";
import {Popup} from "../../../components/Popup.tsx";
import Select from "react-select";
import {ButtonType} from "../../../enums/ButtonType.ts";

export const EventMenuEditPage: React.FC = () => {

 const {menuItem,menuItemActions, setShowDeleteModal, showDeleteModal, navigate, setIngredients,ingredientOptions,selectedIngredients } = useEventMenuEditPage();
 //const fieldRequiredMessage = "Dieses Feld ist erforderlich.";
 //const fieldLengthMessage = "Die Eingabe ist muss zwischen 5 und 64 Zeichen sein.";

 const {
  register,
  handleSubmit,
  //formState: { errors },
  getValues
 } = useForm();



 return(<div>
  <h2 className="text-xl font-semibold text-default-800 mb-4">{menuItem.id>0?"Edit Menu Item":"Menu Item erstellen"}</h2>
  <form onSubmit={handleSubmit(() => menuItemActions.saveMenuItem(getValues()))} >
    <FormInput id={nameof<MenuItemDTO>(e => e.name)}
               defaultValue={menuItem.name}
               label={"Name"}
               type="text"
               register={register}
               isRequired={true}
               minLength={5}
               maxLength={64}/>
   <FormInput id={nameof<MenuItemDTO>(e => e.price)}
              defaultValue={""+menuItem.price}
              label={"Price"}
              type="number"
              step={0.01}
              register={register}
              isRequired={true}
   />
   <label className="mb-2 block text-sm font-medium text-default-900">
    Ingredients
   </label>
   <Select
       isMulti
       name="colors"
       options={ingredientOptions}
       value={selectedIngredients}
       onChange={(values)=>setIngredients(values.map((item)=>{return{id:item.value,name:item.label}}))}
       className="basic-multi-select"
       classNamePrefix="select"/>
   <div className="flex min-h-[60px] items-end ml-auto">

    <br/>
    <Button buttonText={menuItem.id != undefined && menuItem.id > 0 ? "Speichern" : "Erstellen"} className="my-2" type="submit" />
    {menuItem.id != undefined && menuItem.id > 0 && <Button buttonText="Löschen" className="my-2 mx-2"  onClick={() => setShowDeleteModal(true)} />}<Button className="my-2 mx-2" buttonType={ButtonType.Secondary} buttonText={"Zurück"} onClick={()=>navigate("./../")}/>
   </div>

  </form>
  <Popup show={showDeleteModal} onClose={()=>setShowDeleteModal(false)} onAccept={menuItemActions.deleteMenuItem} modalText={"Do you want to delete this menu item?"} acceptText={"Yes"} closeText={"No"}/>
 </div>)
}