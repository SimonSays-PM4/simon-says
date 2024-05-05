import React from "react";
import {useStationView} from "./StationView.hooks.ts";
import {OrderCard} from "../../../components/station/OrderCard.tsx";
import {Badge} from "../../../components/display/Badge.tsx";
import {IngredientCard} from "../../../components/station/IngredientCard.tsx";
import {State} from "../../../gen/api";

export const StationViewComponent: React.FC = () => {
    const {station, orders, ingredientHandling: {ingredients, processIngredient},assemblyHandling:{processMenu,processMenuItem}} = useStationView()
    return <div className="w-full">
        <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
        <h2 className="text-2xl">Station - <b>{station.name}</b> </h2><div><Badge color="green"><>{station.assemblyStation ? orders?.filter((e)=>e.state!=State.Done).length:ingredients.length}</></Badge></div>
        </div>
        <br/>

        {!station.assemblyStation ?<>
        <div className="grid grid-cols-1 md:grid-cols-6 gap-1">
            {...ingredients.map((row) => {
                return <IngredientCard ingredient={row} onClick={()=>processIngredient(row.id)}/>
            })}
        </div>

        </>: <div className="grid grid-cols-1 md:grid-cols-2 gap-1">
            {...orders.filter((e)=>e.state!=State.Done).map((row)=> {
                return <OrderCard onClickMenu={processMenu} onClickMenuItem={processMenuItem} order={row} onClick={()=>console.log("hello")}/>
            })}

        </div>}


    </div>
}