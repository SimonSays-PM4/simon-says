import { IngredientDTO } from "../gen/api";

export class OrderIngredientModel implements IngredientDTO {
    id: number;
    name: string;
    index: number;
    mustBeProduced: boolean;

    constructor(index: number, orderIngredient: IngredientDTO) {
        this.index = index;
        this.id = orderIngredient.id;
        this.name = orderIngredient.name;
        this.mustBeProduced = orderIngredient.mustBeProduced;
    }
}
