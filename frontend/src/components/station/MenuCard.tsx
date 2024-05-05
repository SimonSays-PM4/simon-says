import * as React from "react";
import {OrderMenuDTO, OrderMenuItemDTO, State} from "../../gen/api";
import {MenuItemCard} from "./MenuItemCard.tsx";
type IngredientProps = {
    orderMenu:OrderMenuDTO,
    onClickMenuItem: (id:OrderMenuItemDTO) => void
    onClick: ()=>void

}
export const MenuCard: React.FC<IngredientProps> = ({ orderMenu,onClickMenuItem, onClick}) => {
    return <>
        <a onClick={()=> orderMenu.menuItems.filter((e)=> e.state==State.InProgress).length>0?console.log("blocked"):onClick()}
            className={`${orderMenu.state==State.Done?"bg-green-50":"bg-orange-50"} col-span-2 p-2 bg-white border border-gray-200 rounded-lg shadow`}>

            <h1><b>{orderMenu.name}</b></h1>
            <div className="h-2"/>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-1">
            {orderMenu.menuItems.map((menuItem)=>{
                return <MenuItemCard onClick={()=>onClickMenuItem(menuItem)} orderMenuItem={menuItem}/>
            })}
            </div>
        </a>
    </>
}