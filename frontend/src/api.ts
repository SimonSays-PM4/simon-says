import {EventControllerApi} from "./gen/api";

const API_URL = process.env.VITE_API_URL;

const eventService = new EventControllerApi(undefined,API_URL, undefined);

export{eventService}