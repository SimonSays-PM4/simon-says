import {EventControllerApi, HealthControllerApi} from "./gen/api";

export const API_URL = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;

console.log("API ROUTE: "+API_URL)
console.log("STAGE: "+import.meta.env.MODE)
const eventService = new EventControllerApi(undefined,API_URL, undefined);
const healthService = new HealthControllerApi(undefined,API_URL || import.meta.env.VITE_API_URL, undefined);

export{eventService,healthService}