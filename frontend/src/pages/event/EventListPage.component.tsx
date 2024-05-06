import React from "react";
import {DataTable} from "../../components/data-tables/DataTable";
import {ColumnType} from "../../models/ColumnType";
import {Loader} from "../../components/Loader";
import {useNavigate} from "react-router-dom";
import {useEventListPage} from "./EventListPage.hooks.tsx";
import {Popup} from "../../components/Popup.tsx";
import {EventCreateUpdateDTO, EventDTO} from "../../gen/api";
import {ButtonType} from "../../enums/ButtonType.ts";
import {IoFastFoodOutline} from "react-icons/io5";
import {PiChargingStationDuotone, PiCookieDuotone, PiHamburgerDuotone, PiReceiptDuotone} from "react-icons/pi";
import {MdEditSquare} from "react-icons/md";
import {FaRegTrashAlt} from "react-icons/fa";
import { TbArrowsJoin } from "react-icons/tb";

export const EventListPageComponent: React.FC = () => {
    const { loading, eventActions, showDeletePopup, setShowDeletePopup, data } = useEventListPage()
    const navigate = useNavigate();

    const onEditClick = (row: EventCreateUpdateDTO) => {
        navigate("../event/create/" + row.id)
    }

    const onClickMenuItem = (row: EventCreateUpdateDTO) => {
        navigate("../" + row.id + "/menuItem")
    }

    const onClickMenu = (row: EventCreateUpdateDTO) => {
        navigate("../" + row.id + "/menu")
    }

    const onClickIngredients = (row: EventCreateUpdateDTO) => {
        navigate("../" + row.id + "/ingredients")
    }

    const onClickOrders = (row: EventCreateUpdateDTO) => {
        navigate("../../" + row.id + "/order")
    }

    const onClickStations = (row: EventCreateUpdateDTO) => {
        navigate("../" + row.id + "/station")
    }

    const onDeleteClick = (row: EventDTO) => {
        if (row.id) {
            eventActions.setEventToDelete(row);
            setShowDeletePopup(true);
        }
    }

    const onJoinClick = (row: EventCreateUpdateDTO) => {
        navigate("../../" + row.id + "/join")
    }

    const columns: Array<ColumnType<EventDTO>> = [
        {
            key: "name",
            name: "Name",
            type: "column",
        },
        {
            key: "numberOfTables",
            name: "Anzahl Tische",
            center:true,
            formatter: (row) => {
                return <span className="bg-red-100 text-red-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded-full dark:bg-red-900 dark:text-red-300">{row.numberOfTables}</span>
            },

            type: "column",
        },
        {
            key: "id",
            name: "Menus",
            elementKey: "menuAction",
            type: "action",
            buttonType: ButtonType.Secondary,
            children:<IoFastFoodOutline/>,
            action: onClickMenu
        },
        {
            key: "id",
            name: "Menu Item",
            elementKey: "menuItemAction",
            type: "action",
            buttonType: ButtonType.Secondary,
            children: <PiHamburgerDuotone />,
            action: onClickMenuItem
        },
        {
            key: "id",
            name: "Zutaten",
            elementKey: "ingredientAction",
            type: "action",
            buttonType: ButtonType.Secondary,
            children: <PiCookieDuotone/>,
            action: onClickIngredients
        },
        {
            key: "id",
            name: "Bestellungen",
            elementKey: "orderAction",
            type: "action",
            buttonType: ButtonType.Secondary,
            children: <PiReceiptDuotone  />,
            action: onClickOrders
        },
        {
            key: "id",
            name: "Stationen",
            elementKey: "stationAction",
            type: "action",
            buttonType: ButtonType.Secondary,
            children:<PiChargingStationDuotone/>,
            action: onClickStations
        },
        {
            key: "id",
            name: "Join Event",
            elementKey:"joinAction",
            type: "action",
            children: <TbArrowsJoin/>,
            noText:true,
            action: onJoinClick
        },
        {
            key: "id",
            name: "Edit",
            elementKey: "editAction",
            type: "action",
            children: <MdEditSquare/>,
            noText:true,
            action: onEditClick
        },
        {
            key: "id",
            name: "Delete",
            elementKey:"deleteAction",
            type: "action",
            children: <FaRegTrashAlt/>,
            noText:true,
            action: onDeleteClick
        },
    ];


    return (
        <div className="w-full">
            {loading ? (
                <div className="w-[100px] block mx-auto"><Loader /></div>
            ) : (
                <DataTable<EventDTO> title="Events" columns={columns} rows={data} onCreateClick={() => navigate("../event/create")} />
            )}
            <Popup show={showDeletePopup} onClose={() => setShowDeletePopup(false)} onAccept={eventActions.deleteEvent} modalText={'Delete "' + eventActions.eventToDelete.name + '"'} closeText="Abbrechen" acceptText="Löschen" />
        </div>
    );
}