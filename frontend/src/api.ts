import axios from "axios";
import {
    EventControllerApi,
    IngredientControllerApi,
    MenuItemControllerApi,
    MenuControllerApi, StationControllerApi, OrderControllerApi
} from "./gen/api/api";

export const API_URL = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;

const getEventService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    return new EventControllerApi(undefined, API_URL, axiosInstance);
};
const getIngredientService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    return new IngredientControllerApi(undefined, API_URL, axiosInstance);
};
const getMenuItemService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    return new MenuItemControllerApi(undefined, API_URL, axiosInstance);
};
const getMenuService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    return new MenuControllerApi(undefined, API_URL, axiosInstance);
};

const getOrderService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    return new OrderControllerApi(undefined, API_URL, axiosInstance);
};

const getStationService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    return new StationControllerApi(undefined, API_URL, axiosInstance);
};

export { getEventService, getIngredientService, getMenuService, getMenuItemService, getStationService, getOrderService };
