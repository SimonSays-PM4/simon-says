import { MenuDTO } from "../gen/api";
import { OrderMenuItemModel } from "./OrderMenuItemModel";

export class OrderMenuModel implements MenuDTO {
    id: number;
    name: string;
    menuItems: OrderMenuItemModel[];
    price: number;
    index: number;

    constructor(index: number, orderMenu: MenuDTO) {
        this.index = index;
        this.id = orderMenu.id;
        this.name = orderMenu.name;
        this.menuItems = orderMenu.menuItems.map((menuItem, index) => new OrderMenuItemModel(index, menuItem));
        this.price = orderMenu.price;
    }
}
