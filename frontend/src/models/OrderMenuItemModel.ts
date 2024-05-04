import { OrderMenuItemDTO, State } from "../gen/api";
import { OrderIngredientModel } from "./OrderIngredientModel";

export class OrderMenuItemModel implements OrderMenuItemDTO {
    id: number;
    name: string;
    ingredients: OrderIngredientModel[];
    price: number;
    state: State;
    index: number;

    constructor(index: number, orderMenuItem: OrderMenuItemDTO) {
        this.index = index;
        this.id = orderMenuItem.id;
        this.name = orderMenuItem.name;
        this.ingredients = orderMenuItem.ingredients.map(
            (ingredient, index) => new OrderIngredientModel(index, ingredient)
        );
        this.price = orderMenuItem.price;
        this.state = orderMenuItem.state;
    }
}
