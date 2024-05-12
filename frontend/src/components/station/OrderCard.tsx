import * as React from "react";
import {OrderDTO, OrderMenuItemDTO, State} from "../../gen/api";
import {MenuItemCard} from "./MenuItemCard.tsx";
import {MenuCard} from "./MenuCard.tsx";
import {Badge} from "../display/Badge.tsx";
type OrderProps = {
    order: OrderDTO,
    onClick:()=>void,
    onClickMenu: (id:number)=>void,
    onClickMenuItem: (id:OrderMenuItemDTO) => void
}
export const OrderCard: React.FC<OrderProps> = ({onClick, order, onClickMenuItem, onClickMenu}) => {

    return <>
        <a onClick={onClick}
           className="p-2 bg-white border border-gray-200 rounded-lg shadow">
            <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
                <h1>Order <b>#{order.id}</b></h1><p className="text-xs"><Badge color={order.state==State.Done?"green":"yellow"}>{order.state}</Badge></p>
            </div>
            <div className="h-2"/>
            <div className="grid grid-flow-row-dense grid-cols-2 gap-1">
            {order?.menus?.map((menu)=> {
                return <MenuCard onClickMenuItem={onClickMenuItem} onClick={()=>onClickMenu(menu.id)} orderMenu={menu} />
            })}
            {order?.menuItems?.map((item)=> {
                return <MenuItemCard onClick={()=>onClickMenuItem(item)} orderMenuItem={item} />
            })}
            </div>
        </a>
    </>
}