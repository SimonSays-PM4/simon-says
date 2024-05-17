import { useCallback, useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { OrderDTO, OrderIngredientDTO, OrderMenuItemDTO, State, StationDTO } from "../../gen/api";
import { EventContext } from "../../providers/EventContext.tsx";
import { AppContext } from "../../providers/AppContext.tsx";
import { getOrderService, getStationService } from "../../api.ts";
import { NotificationType } from "../../enums/NotificationType.ts";
import { io } from "socket.io-client";

type IngredientHandling = {
    processIngredient: (id: number) => void;
    ingredients: OrderIngredientDTO[];
    doneIngredients: OrderIngredientDTO[];
};

type AssemblyStationHandling = {
    processMenuItem: (orderMenuItemDTO: OrderMenuItemDTO) => void;
    processMenu: (id: number) => void;
};

type StationAction = {
    ingredientHandling: IngredientHandling;
    assemblyHandling: AssemblyStationHandling;
    isLoading: boolean;
    station: StationDTO;
    orders: OrderDTO[];
    isConnected: boolean;
    socketId: string | undefined;
    removeFromDone: (ing: number) => void;
};

export const useStationView = (): StationAction => {
    const { loginInfo, addNotification } = useContext(AppContext);
    const { eventId } = useContext(EventContext);
    const { stationId } = useParams();

    const [station, setStation] = useState<StationDTO>({ id: 0, assemblyStation: false, ingredients: [], name: "" });
    const [isLoading, setIsLoading] = useState(false);

    const [ingredients, setIngredients] = useState<OrderIngredientDTO[]>([]);
    const [doneIngredients, setDoneIngredients] = useState<Array<OrderIngredientDTO>>([]);
    const [orders, setOrders] = useState<OrderDTO[]>([]);

    const stationService = getStationService(loginInfo.userName, loginInfo.password);
    const orderService = getOrderService(loginInfo.userName, loginInfo.password);
    const [expiringIngredients, setExpiringIngredients] = useState<{ time: number; id: number }[]>([]);
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [socketId, setSocketId] = useState<string | undefined>(undefined);

    useEffect(() => {
        console.log(socketId);
        if (!socketId && station.id > 0) {
            const url = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;
            console.log("url", url + `/socket-api/v1/event/${eventId}/station/view/${stationId}`);
            const socket = io(url + `/socket-api/v1/event/${eventId}/station/view/${stationId}`);

            socket.connect();
            socket.on("connect", () => {
                setIsConnected(true);
                setSocketId(socket.id ?? "-");
                console.log(socket.id);
            });

            socket.on("disconnect", () => {
                setIsConnected(false);
            });

            socket.on("initial-data", (data: OrderIngredientDTO[]) => {
                console.log("initial-data", data);
                setIngredients(data.filter((ing) => ing.state !== State.Done));
            });

            socket.on("change", (data: OrderIngredientDTO) => {
                console.log("change", data);
                if (data.state !== State.Done) {
                    console.log(ingredients);
                    setIngredients(() => [...ingredients, data]);
                }
            });

            socket.on("remove", (id) => {
                console.log("remove", id);
            });

            socket.on("application-error", (message) => {
                console.log("application-error", message);
            });

            return () => {
                socket.off("connect");
                socket.off("disconnect");
                socket.off("initial-data");
                socket.off("onChange");
                socket.off("onRemove");
                socket.off("application-error");
                socket.disconnect();
            };
        }
    }, [station]);

    useEffect(() => {
        if (eventId > 0 && stationId) {
            setIsLoading(true);
            stationService
                .getStation(eventId, Number(stationId))
                .then((response) => {
                    setStation(response.data);
                })
                .catch(() => {
                    addNotification(NotificationType.ERR, "Failed to load station");
                })
                .finally(() => {
                    setIsLoading(false);
                });
        }
    }, []);

    const removeFromDone = useCallback(
        (ing: number) => {
            setDoneIngredients([...doneIngredients.filter((doneIng) => doneIng.id != ing)]);
        },
        [doneIngredients]
    );

    const processIngredient = useCallback(
        (id: number) => {
            const ings = ingredients.filter((ingi) => ingi.id == id);
            orderService.updateOrderIngredientState(eventId, id).then(() => {
                const item1 = ings.at(0);

                if (item1) {
                    item1.state = State.Done;
                    setDoneIngredients([...doneIngredients, item1]);
                    setExpiringIngredients([...expiringIngredients, { time: Date.now(), id: item1.id }]);
                    setIngredients([...ingredients.filter((ing) => ing.id != id)]);

                    console.log(expiringIngredients);
                }
            });
        },
        [ingredients, doneIngredients, expiringIngredients]
    );

    const processMenu = useCallback(
        (id: number) => {
            orderService.updateOrderMenuState(eventId, id);
        },
        [eventId]
    );

    const processMenuItem = useCallback(
        (orderMenuItemDTO: OrderMenuItemDTO) => {
            orderMenuItemDTO.ingredients.forEach((ing) => {
                processIngredient(ing.id);
            });
            orderService.updateOrderMenuItemState(eventId, orderMenuItemDTO.id);
        },
        [eventId]
    );

    const ingredientHandling: IngredientHandling = {
        processIngredient,
        ingredients,
        doneIngredients
    };

    const assemblyHandling: AssemblyStationHandling = {
        processMenu,
        processMenuItem
    };

    return { isLoading, station, ingredientHandling, orders, assemblyHandling, removeFromDone, isConnected, socketId };
};
