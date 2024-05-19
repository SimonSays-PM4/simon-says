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
    const url = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;
    const { loginInfo, addNotification } = useContext(AppContext);
    const { eventId } = useContext(EventContext);
    const { stationId } = useParams();

    const [station, setStation] = useState<StationDTO>({ id: 0, assemblyStation: false, ingredients: [], name: "" });
    const [isLoading, setIsLoading] = useState(false);

    const [ingredients, setIngredients] = useState<OrderIngredientDTO[]>([]);
    const [doneIngredients, setDoneIngredients] = useState<Array<OrderIngredientDTO>>([]);
    const [orders, setOrders] = useState<OrderDTO[]>([]);
    const [expiringIngredients, setExpiringIngredients] = useState<{ time: number; id: number }[]>([]);
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [socketId, setSocketId] = useState<string | undefined>(undefined);

    const stationService = getStationService(loginInfo.userName, loginInfo.password);
    const orderService = getOrderService(loginInfo.userName, loginInfo.password);

    const isOrderArray = (data: OrderIngredientDTO[] | OrderDTO[]): data is OrderDTO[] => {
        return "totalPrice" in data[0];
    };

    const isOrder = (data: OrderIngredientDTO | OrderDTO): data is OrderDTO => {
        return "totalPrice" in data;
    };

    useEffect(() => {
        if (!socketId && station.id > 0) {
            const socketUrl = station.assemblyStation
                ? url + `/socket-api/v1/event/${eventId}/station/assembly`
                : url + `/socket-api/v1/event/${eventId}/station/view/${stationId}`;

            const socket = io(socketUrl);

            socket.connect();
            socket.on("connect", () => {
                setIsConnected(true);
                setSocketId(socket.id ?? "-");
                console.log(socket.id);
            });

            socket.on("disconnect", () => {
                setIsConnected(false);
                setSocketId(undefined);
            });

            socket.on("initial-data", (data: OrderIngredientDTO[] | OrderDTO[]) => {
                console.log("initial-data", data);
                if (data.length > 0 && isOrderArray(data)) {
                    const orderDtos = data as OrderDTO[];
                    setOrders(orderDtos.filter((order) => order.state !== State.Done));
                } else {
                    const orderIngredientDtos = data as OrderIngredientDTO[];
                    setIngredients(orderIngredientDtos.filter((ing) => ing.state !== State.Done));
                }
            });

            socket.on("change", (data: OrderIngredientDTO | OrderDTO) => {
                if (isOrder(data)) {
                    const orderDto = data as OrderDTO;
                    console.log("change", orderDto);
                    if (orderDto.state === State.Done) {
                        setOrders((prevOrders) => prevOrders.filter((order) => order.id !== orderDto.id));
                    } else {
                        setOrders((prevOrders) => {
                            const existing = prevOrders.find((order) => order.id === orderDto.id);
                            if (existing) {
                                return prevOrders.map((order) => (order.id === orderDto.id ? orderDto : order));
                            } else {
                                return [...prevOrders, orderDto];
                            }
                        });
                    }
                } else {
                    const ingredientDto = data as OrderIngredientDTO;
                    console.log("change", ingredientDto);
                    if (ingredientDto.state === State.Done) {
                        setIngredients((prevIngredients) =>
                            prevIngredients.filter((ing) => ing.id !== ingredientDto.id)
                        );
                    } else {
                        setIngredients((prevIngredients) => {
                            const existing = prevIngredients.find((ing) => ing.id === ingredientDto.id);
                            if (existing) {
                                return prevIngredients.map((ing) =>
                                    ing.id === ingredientDto.id ? ingredientDto : ing
                                );
                            } else {
                                return [...prevIngredients, ingredientDto];
                            }
                        });
                    }
                }
            });

            socket.on("remove", (data: OrderIngredientDTO | OrderDTO) => {
                console.log("remove", data);
                if (isOrder(data)) {
                    const orderDto = data as OrderDTO;
                    setOrders((prevOrders) => prevOrders.filter((order) => order.id !== orderDto.id));
                } else {
                    const ingredientDto = data as OrderIngredientDTO;
                    setIngredients((prevIngredients) => prevIngredients.filter((ing) => ing.id !== ingredientDto.id));
                }
            });

            socket.on("application-error", (message) => {
                console.log("application-error", message);
                addNotification(NotificationType.ERR, message);
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
            const ingredient = ingredients.find((ingi) => ingi.id == id);
            orderService.updateOrderIngredientState(eventId, id).then(() => {
                if (ingredient) {
                    ingredient.state = State.Done;
                    setDoneIngredients(() => [...doneIngredients, ingredient]);
                    setExpiringIngredients(() => [...expiringIngredients, { time: Date.now(), id: ingredient.id }]);
                    setIngredients((prevIngredients) => [...prevIngredients.filter((ing) => ing.id != id)]);
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
