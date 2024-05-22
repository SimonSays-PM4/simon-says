import * as React from "react";
import { OrderMenuDTO, OrderMenuItemDTO, State } from "../../gen/api";
import { MenuItemCard } from "./MenuItemCard.tsx";
import { FaRegCheckCircle } from "react-icons/fa";
import { PiClockDuotone } from "react-icons/pi";

type IngredientProps = {
    orderMenu: OrderMenuDTO,
    onClickMenuItem: (id: OrderMenuItemDTO) => void
    onClick: () => void
}

export const MenuCard: React.FC<IngredientProps> = ({ orderMenu, onClickMenuItem, onClick }) => {
    let state: State = State.Done;
    if (orderMenu.menuItems.filter((e) => e.state == State.InProgress).length > 0) {
        state = State.InProgress;
    }

    return (
        <a onClick={() => state == State.InProgress ? console.log("blocked") : onClick()}
            className={`${orderMenu.state == State.Done ? "bg-green-50" : "bg-orange-50"} col-span-2 p-2 border border-gray-200 rounded-lg shadow`}>

            <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
                <h1><b>{orderMenu.name}</b></h1>
                <p>{orderMenu.state == State.Done ? <FaRegCheckCircle color="green" /> : <PiClockDuotone color="orange" className="animate-pulse" />}</p>
            </div>
            <div className="h-2" />
            <div className="grid grid-cols-1 md:grid-cols-2 gap-1">
                {orderMenu.menuItems.map((menuItem) => {
                    return <MenuItemCard onClick={() => onClickMenuItem(menuItem)} orderMenuItem={menuItem} />
                })}
            </div>
        </a>
    );
}