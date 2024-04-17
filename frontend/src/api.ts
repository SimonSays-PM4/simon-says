import axios from "axios";
import {
    EventControllerApi,
    HealthControllerApi,
    IngredientControllerApi,
    MenuItemControllerApi,
    MenuControllerApi
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

const healthService = new HealthControllerApi(undefined, API_URL, undefined);

export { healthService, getEventService, getIngredientService, getMenuService, getMenuItemService };
