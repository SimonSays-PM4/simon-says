import * as React from "react";
import { OrderMenuItemDTO, State } from "../../gen/api";
import { FaRegCheckCircle } from "react-icons/fa";
import { PiClockDuotone } from "react-icons/pi";
import { useState } from "react";
import { ImCheckboxChecked } from "react-icons/im";

type MenuItemProps = {
    orderMenuItem: OrderMenuItemDTO,
    onClick: () => void
}

export const MenuItemCard: React.FC<MenuItemProps> = ({ orderMenuItem, onClick }) => {
    const [pressed, setPressed] = useState<boolean>(false);

    const onPress = () => {
        if (orderMenuItem.state != State.Done) {
            if (pressed) {
                onClick();
            } else {
                setPressed(true);
            }
        }
    }

    let classOfCard = `${orderMenuItem.state == State.Done ? "bg-green-50" : "bg-white"} col-span-1 p-2 border border-gray-200 rounded-lg shadow`;

    if (pressed && orderMenuItem.state != State.Done) {
        classOfCard = `shadow bg-gray-100 col-span-1 p-3 border border-100 rounded-lg animate-pulse`
    }

    return (
        <a onClick={onPress} className={classOfCard}>
            <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
                <h1><b>{orderMenuItem.name}</b></h1>
                <p>{orderMenuItem.state == State.Done ? <FaRegCheckCircle color="green" /> : pressed ? <ImCheckboxChecked /> : <PiClockDuotone color="orange" className="animate-pulse" />}</p>
            </div>

            <hr className="h-px my-1 bg-gray-500 border-0" />

            <div className="grid grid-cols-1">
                {orderMenuItem.ingredients.map((ing) => {
                    return <><div>- {ing.name}</div></>
                })}
            </div>
        </a>
    );
};