import {EventControllerApi} from "./gen/api";

const API_URL = JSON.stringify(import.meta.env.__API_URL__);
const eventService = new EventControllerApi(undefined,API_URL,undefined);

export{eventService}