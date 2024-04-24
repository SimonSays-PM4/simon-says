import { useCallback, useContext, useEffect, useState } from "react";
import { EventContext } from "../../providers/EventContext";
import { getOrderService } from "../../api";
import { OrderDTO, State } from "../../gen/api";
import { AppContext } from "../../providers/AppContext";

type OrderActions = {
    deleteOrder: () => void,
    setOrderToDelete: (ingredient: OrderDTO) => void,
    orderToDelete: OrderDTO
};
type OrderListPageReturnProps = {
    orderActions: OrderActions
    isLoading: boolean,
    showDeletePopup: boolean
    setShowDeletePopup: (show: boolean) => void,
    data: OrderDTO[]
};

export const useOrderListPage = (): OrderListPageReturnProps => {
    const { eventId } = useContext(EventContext);
    const [orderToDelete, setOrderToDelete] = useState<OrderDTO>({ id: 0, tableNumber: 0, totalPrice: 0, menuItems: [], menus: [], state: State.InProgress });
    const [isLoading, setIsLoading] = useState(false);
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [data, setData] = useState<OrderDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const orderService = getOrderService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        if (!showDeletePopup) {
            reloadOrders();
        }
    }, [showDeletePopup]);

    const reloadOrders = useCallback(() => {
        try {
            setIsLoading(true);
            orderService.getOrders(eventId).then((response) => {
                setData(response.data);
            });
        }
        catch (error) {
            // TOOD: handle error
        }
        finally {
            setIsLoading(false);
        }
    }, []);

    const deleteOrder = useCallback(() => {
        try {
            if (orderToDelete.id && orderToDelete.id > 0) {
                setIsLoading(true);

                orderService.deleteOrder(eventId, orderToDelete.id).then(() => {
                    setShowDeletePopup(false);
                });
            }
        }
        catch (error) {
            // TOOD: handle error
        }
        finally {
            setIsLoading(false);
        }
    }, [orderToDelete.id]);

    const orderActions: OrderActions = {
        deleteOrder,
        setOrderToDelete,
        orderToDelete
    };

    return { orderActions, isLoading, showDeletePopup, setShowDeletePopup, data };
}