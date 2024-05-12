import {useCallback, useContext, useEffect, useState} from "react";
import {getOrderService, getStationService} from "../../../api.ts";
import {AppContext} from "../../../providers/AppContext.tsx";
import {EventContext} from "../../../providers/EventContext.tsx";
import {useParams} from "react-router-dom";
import {OrderDTO, OrderIngredientDTO, OrderMenuItemDTO, State, StationDTO} from "../../../gen/api";
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
    orders: OrderDTO[],
    removeFromDone:(ing:number) =>void
}

export const useStationView = ():StationAction  => {
    const {loginInfo, addNotification} = useContext(AppContext);
    const { eventId } = useContext(EventContext);
    const {stationId} = useParams();

    const [station, setStation] = useState<StationDTO>({id:0, assemblyStation:false,ingredients:[],name:""});
    const [isLoading, setIsLoading] = useState(false);

    const [ingredients, setIngredients] = useState<OrderIngredientDTO[]>([])
    const [doneIngredients, setDoneIngredients] = useState<Array<OrderIngredientDTO>>([]);
    const [orders, setOrders] = useState<OrderDTO[]>([])

    const stationService = getStationService(loginInfo.userName, loginInfo.password);
    const orderService = getOrderService(loginInfo.userName, loginInfo.password);
    const [expiringIngredients,setExpiringIngredients] = useState<{time:number, id:number}[]>([]);

    const reloadStationView = useCallback(()=>{
        if (eventId>0 && stationId) {
            setIsLoading(true);
            stationService.getStation(eventId,Number(stationId)).then((response)=>{
                setStation(response.data);
                stationService.getStationView(eventId,Number(stationId)).then((response)=> {
                    setIngredients(response.data);
                    console.log("helol")

                })
            }).catch(() => {
                addNotification(NotificationType.ERR, "Failed to load station");
            })
        }
    }, [stationId,eventId,expiringIngredients,doneIngredients])

    const removeFromDone = useCallback((ing:number)=> {
        setDoneIngredients([...doneIngredients.filter(doneIng=>doneIng.id != ing)])
    },[doneIngredients])

    const reloadAssemblyStation = useCallback(()=>{
        if (eventId>0 && stationId) {
            setIsLoading(true);
            stationService.getAssemblyStationView(eventId).then((response) => {
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
        const ings = ingredients.filter((ingi)=>ingi.id==id);
        orderService.updateOrderIngredientState(eventId,id).then(()=> {
            const item1 = ings.at(0);

            if (item1) {
                item1.state = State.Done;
                setDoneIngredients([...doneIngredients,item1]);
                setExpiringIngredients([...expiringIngredients, {time:Date.now(),id:item1.id}]);
                console.log(expiringIngredients)
            }
            reloadStationView();
        })
    },[ingredients,doneIngredients,expiringIngredients])

    const processMenu = useCallback((id:number)=> {
        orderService.updateOrderMenuState(eventId,id).then(()=> {
            reloadStationView();
        })
    },[eventId])

    const processMenuItem = useCallback((orderMenuItemDTO:OrderMenuItemDTO)=> {
        orderMenuItemDTO.ingredients.forEach((ing)=> {
            processIngredient(ing.id);
        })
        orderService.updateOrderMenuItemState(eventId,orderMenuItemDTO.id).then(()=> {
            reloadStationView();
        })
    },[eventId])

    const ingredientHandling:IngredientHandling = {
        processIngredient,
        ingredients,
        doneIngredients
    }

    const assemblyHandling:AssemblyStationHandling = {
        processMenu,
        processMenuItem
    }

    return {isLoading,station, ingredientHandling, orders, assemblyHandling,removeFromDone}
}