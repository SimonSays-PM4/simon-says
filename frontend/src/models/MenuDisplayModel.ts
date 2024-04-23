import { MenuItemDTO, MenuDTO } from "../gen/api";

export class MenuDisplayModel implements MenuDTO {
    id: number;
    name: string;
    menuItems: MenuItemDTO[];
    price: number;
    menuItemsString: string;

    constructor(id: number, name: string, menuItems: MenuItemDTO[], price: number, menuItemsString: string) {
        this.id = id;
        this.name = name;
        this.menuItems = menuItems;
        this.price = price;
        this.menuItemsString = menuItemsString;
    }
}
