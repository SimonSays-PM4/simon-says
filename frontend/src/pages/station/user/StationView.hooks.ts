import {useCallback, useContext, useEffect, useState} from "react";
import {getOrderService, getStationService} from "../../../api.ts";
import {AppContext} from "../../../providers/AppContext.tsx";
import {EventContext} from "../../../providers/EventContext.tsx";
import {useParams} from "react-router-dom";
import {OrderDTO, OrderIngredientDTO, OrderMenuItemDTO, StationDTO} from "../../../gen/api";
import {NotificationType} from "../../../enums/NotificationType.ts";
type IngredientHandling = {
    processIngredient: (id:number) => void,
    ingredients:OrderIngredientDTO[],
    doneIngredients: OrderIngredientDTO[]
}

type AssemblyStationHandling = {
    processMenuItem: (orderMenuItemDTO:OrderMenuItemDTO)=>void,
    processMenu: (id:number) => void
}
type StationAction = {
    ingredientHandling:IngredientHandling,
    assemblyHandling:AssemblyStationHandling,
    isLoading:boolean,
    station:StationDTO,
    orders: OrderDTO[]
}

export const useStationView = ():StationAction  => {
    const {loginInfo, addNotification} = useContext(AppContext);
    const { eventId } = useContext(EventContext);
    const {stationId} = useParams();

    const [station, setStation] = useState<StationDTO>({id:0, assemblyStation:false,ingredients:[],name:""});
    const [isLoading, setIsLoading] = useState(false);

    const [ingredients, setIngredients] = useState<OrderIngredientDTO[]>([])
    const [orders, setOrders] = useState<OrderDTO[]>([])

    const stationService = getStationService(loginInfo.userName, loginInfo.password);
    const orderService = getOrderService(loginInfo.userName, loginInfo.password);

    const reloadStationView = useCallback(()=>{
        if (eventId>0 && stationId) {
            setIsLoading(true);
            stationService.getStation(eventId,Number(stationId)).then((response)=>{
                setStation(response.data);
                stationService.getStationView(eventId,Number(stationId)).then((response)=> {
                    setIngredients(response.data);
                })
            }).catch(() => {
                addNotification(NotificationType.ERR, "Failed to load station");
            })
        }
    }, [stationId,eventId])

    const reloadAssemblyStation = useCallback(()=>{
        if (eventId>0 && stationId) {
            setIsLoading(true);
            orderService.getOrders(eventId).then((response) => {
                setOrders(response.data);
            }).catch(() => {
                addNotification(NotificationType.ERR, "Failed to load station");
            }).finally(()=> {
                setIsLoading(false);
            })
        }
    }, [stationId,eventId])

    useEffect(()=> {
        reloadAssemblyStation();
        reloadStationView();
    }, [stationId, eventId])

    useEffect(() => {
        const intervalId = setInterval(() => {
            reloadStationView();
        }, 2000);

        return () => clearInterval(intervalId);
    }, [stationId,eventId]);

    useEffect(() => {
        const intervalId = setInterval(() => {
            reloadAssemblyStation();
        }, 2000);

        return () => clearInterval(intervalId);
    }, [stationId,eventId])

    const processIngredient = useCallback((id:number)=> {
        orderService.updateOrderIngredientState(eventId,id).then(()=> {
            console.log("removed")
            reloadStationView();
        })
    },[])

    const processMenu = useCallback((id:number)=> {
        orderService.updateOrderMenuState(eventId,id).then(()=> {
            console.log("removed")
            reloadStationView();
        })
    },[eventId])

    const processMenuItem = useCallback((orderMenuItemDTO:OrderMenuItemDTO)=> {
        orderMenuItemDTO.ingredients.forEach((ing)=> {
            processIngredient(ing.id);
        })
        orderService.updateOrderMenuItemState(eventId,orderMenuItemDTO.id).then(()=> {
            console.log("removed")
            reloadStationView();
        })
    },[eventId])

    const ingredientHandling:IngredientHandling = {
        processIngredient,
        ingredients,
        doneIngredients:[]
    }

    const assemblyHandling:AssemblyStationHandling = {
        processMenu,
        processMenuItem
    }

    return {isLoading,station, ingredientHandling, orders, assemblyHandling}
}