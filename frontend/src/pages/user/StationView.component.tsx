import React from "react";
import { useStationView } from "./StationView.hooks.ts";
import { State } from "../../gen/api";
import { IngredientCard } from "../../components/station/IngredientCard.tsx";
import { OrderCard } from "../../components/station/OrderCard.tsx";
import { Badge } from "../../components/display/Badge.tsx";
import { ButtonType } from "../../enums/ButtonType.ts";
import { IoIosArrowBack } from "react-icons/io";
import { Button } from "../../components/Button.tsx";
import { useNavigate, } from "react-router-dom";
import { Loader } from "../../components/Loader.tsx";

export const StationViewComponent: React.FC = () => {
    const { station, orders, ingredientHandling: { ingredients, processIngredient, doneIngredients }, assemblyHandling: { processMenu, processMenuItem }, removeFromDone, isLoading, isConnected, socketId } = useStationView();
    const navigate = useNavigate();

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <>
                    <div className="flex flex-wrap gap-4 sm:justify-between lg:flex-nowrap">
                        <h2 className="text-2xl"><div className="flex flex-wrap gap-4"><Button onClick={() => navigate("./../../")} buttonType={ButtonType.Primary}><IoIosArrowBack /></Button>Station  -<b>{station?.name}</b></div></h2><div><Badge color="green"><>{station?.assemblyStation ? orders?.filter((e) => e.state != State.Done).length : ingredients.length}</></Badge></div>
                    </div>
                    <br />

                    <p>{isConnected ? "Connected" : "Disconnected"} - {socketId}</p>

                    {!station?.assemblyStation ?
                        <>
                            <div className="grid grid-cols-1 md:grid-cols-6 gap-1">
                                {...doneIngredients.map((row) => {
                                    return <IngredientCard ingredient={row} onClick={() => removeFromDone(row.id)} />
                                })}

                                {...ingredients.map((row) => {
                                    return <IngredientCard ingredient={row} onClick={() => processIngredient(row.id)} />
                                })}
                            </div>
                        </>
                        : <div className="grid grid-cols-1 md:grid-cols-2 gap-1">
                            {...orders.filter((e) => e.state != State.Done).map((row) => {
                                return <OrderCard onClickMenu={processMenu} onClickMenuItem={processMenuItem} order={row} onClick={() => console.log("hello")} />
                            })}

                        </div>}
                </>
            )}

        </div>);
}