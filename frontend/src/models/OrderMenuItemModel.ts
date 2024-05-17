import { MenuItemDTO } from "../gen/api";
import { OrderIngredientModel } from "./OrderIngredientModel";

export class OrderMenuItemModel implements MenuItemDTO {
    id: number;
    name: string;
    ingredients: OrderIngredientModel[];
    price: number;
    index: number;

    constructor(index: number, orderMenuItem: MenuItemDTO) {
        this.index = index;
        this.id = orderMenuItem.id;
        this.name = orderMenuItem.name;
        this.ingredients = orderMenuItem.ingredients.map(
            (ingredient, index) => new OrderIngredientModel(index, ingredient)
        );
        this.price = orderMenuItem.price;
    }
}
