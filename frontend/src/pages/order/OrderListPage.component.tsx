import React from "react";
import { DataTable } from "../../components/data-tables/DataTable";
import { ColumnType } from "../../models/ColumnType";
import { Loader } from "../../components/Loader";
import { OrderDTO, State } from "../../gen/api";
import { useNavigate } from "react-router-dom";
import { Popup } from "../../components/Popup.tsx";
import { useOrderListPage } from "./OrderListPage.hooks.tsx";
import {FaRegTrashAlt} from "react-icons/fa";
import {PiReceiptDuotone} from "react-icons/pi";
import { PrintJobStatusIndicator } from "../../components/PrintJobStatusIndicator.tsx";

export const OrderListPageComponent: React.FC = () => {
    const { isLoading, orderActions, showDeletePopup, setShowDeletePopup, data } = useOrderListPage();
    const navigate = useNavigate();

    const onDeleteClick = (row: OrderDTO) => {
        orderActions.setOrderToDelete(row);
        setShowDeletePopup(true);
    };

    const columns: Array<ColumnType<OrderDTO>> = [
        {
            key: "id",
            name: "Id",
            type: "column"
        },
        {
            key: "totalPrice",
            name: "Preis",
            type: "column",
            formatter: (order) => `${order.totalPrice.toFixed(2)} CHF`
        },
        {
            key: "takeAway",
            name: "Take Away",
            type: "column",
            formatter: (order) => `${order.isTakeAway ? "Ja" : "Nein"}`,
        },
        {
            key: "tableNumber",
            name: "Tischnummer",
            type: "column"
        },
        {
            key: "state",
            name: "Status",
            type: "column",
            formatter: (order) => `${order.state === State.InProgress ? "In Arbeit" : "Abgeschlossen"}`,
        },
        {
            key: "id",
            elementKey: "printStatus",
            name: "Druckstatus",
            type: "column",
            formatter: (order) => <PrintJobStatusIndicator order={order}/>,
        },
        {
            key: "id",
            name: "Löschen",
            elementKey:"deleteAction",
            type: "action",
            children: <FaRegTrashAlt/>,
            noText:true,
            action: onDeleteClick
        },
    ];

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<OrderDTO> title="Bestellungen" onBackClick={() => navigate("./..")} icon={<PiReceiptDuotone />} columns={columns} rows={data} onCreateClick={() => navigate(`../create`)} />
            )}

            <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={orderActions.deleteOrder} modalText={'Möchten Sie die Bestellung löschen?'} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}