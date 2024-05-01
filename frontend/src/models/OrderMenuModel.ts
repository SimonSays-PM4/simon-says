import { OrderMenuDTO, State } from "../gen/api";
import { OrderMenuItemModel } from "./OrderMenuItemModel";

export class OrderMenuModel implements OrderMenuDTO {
    id: number;
    name: string;
    menuItems: OrderMenuItemModel[];
    price: number;
    state: State;
    index: number;

    constructor(index: number, orderMenu: OrderMenuDTO) {
        this.index = index;
        this.id = orderMenu.id;
        this.name = orderMenu.name;
        this.menuItems = orderMenu.menuItems.map((menuItem, index) => new OrderMenuItemModel(index, menuItem));
        this.price = orderMenu.price;
        this.state = orderMenu.state;
    }
}
