import axios from "./gen/api/node_modules/axios";
import { EventControllerApi, HealthControllerApi, IngredientControllerApi } from "./gen/api/api";

export const API_URL = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;

console.log("API ROUTE: " + API_URL);
console.log("STAGE: " + import.meta.env.MODE);

const getEventService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    console.log(username);
    console.log(password);
    return new EventControllerApi(undefined, API_URL, axiosInstance);
};
const getIngredientService = (username: string, password: string) => {
    const axiosInstance = axios.create({ auth: { username: username, password: password } });
    console.log(username);
    console.log(password);
    return new IngredientControllerApi(undefined, API_URL, axiosInstance);
};
const healthService = new HealthControllerApi(undefined, API_URL, undefined);

export { healthService, getEventService, getIngredientService };
