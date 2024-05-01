import { OrderIngredientDTO, State } from "../gen/api";

export class OrderIngredientModel implements OrderIngredientDTO {
    id: number;
    name: string;
    state: State;
    index: number;

    constructor(index: number, orderIngredient: OrderIngredientDTO) {
        this.index = index;
        this.id = orderIngredient.id;
        this.name = orderIngredient.name;
        this.state = orderIngredient.state;
    }
}
