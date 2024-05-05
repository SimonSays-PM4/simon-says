import * as React from "react";
import {OrderMenuItemDTO, State} from "../../gen/api";
import {FaRegCheckCircle} from "react-icons/fa";
import {PiClockDuotone} from "react-icons/pi";
type IngredientProps = {
    orderMenuItem:OrderMenuItemDTO,
    onClick: ()=>void

}
export const MenuItemCard: React.FC<IngredientProps> = ({ orderMenuItem,onClick}) => {


    return<>
        <a onClick={onClick}
            className={`${orderMenuItem.state==State.Done?"bg-green-50":"bg-orange-50"} col-span-1 p-2 border border-gray-200 rounded-lg shadow hover:bg-gray-100`}>
            <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
                <h1><b>{orderMenuItem.name}</b></h1><p>{orderMenuItem.state == State.Done?<FaRegCheckCircle color="green"/>:<PiClockDuotone color="orange" className="animate-pulse"/>}</p>
            </div>
            <hr className="h-px my-1 bg-gray-500 border-0"></hr>
            <div className="grid grid-cols-2">
                {orderMenuItem.ingredients.map((ing)=>{
                    return <><div>{ing.name}</div><div className="justify-self-end">{ing.state == State.Done?<FaRegCheckCircle color="green"/>:<PiClockDuotone color="orange" className="animate-pulse"/>}</div></>
                })}
            </div>

        </a>
    </>
}