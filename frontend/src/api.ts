import {EventControllerApi, HealthControllerApi, IngredientControllerApi, MenuItemControllerApi} from "./gen/api";

export const API_URL = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;
const eventService = new EventControllerApi(undefined, API_URL, undefined);
const ingredientService = new IngredientControllerApi(undefined, API_URL, undefined);

const menuItemService = new MenuItemControllerApi(undefined, API_URL, undefined);
const healthService = new HealthControllerApi(undefined, API_URL || import.meta.env.VITE_API_URL, undefined);

export { eventService, healthService, ingredientService, menuItemService };
