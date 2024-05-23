import * as React from "react";
import { OrderIngredientDTO, State } from "../../gen/api";
import { FaRegCheckCircle } from "react-icons/fa";
import { PiClockDuotone } from "react-icons/pi";

type IngredientProps = {
    ingredient: OrderIngredientDTO,
    onClick: () => void,
}

export const IngredientCard: React.FC<IngredientProps> = ({ onClick, ingredient }) => {
    return (<>
        <a onClick={onClick} className="grid grid-cols-1 max-w-sm p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100">

            <h5 className="mb-6 text-2xl font-bold text-center tracking-tight text-gray-900">{ingredient.name}</h5>
            <p className="justify-self-center mb-5 text-2xl tracking-tight text-gray-900">
                {ingredient.state == State.Done ? <FaRegCheckCircle className="justify-end" color="green" /> : <PiClockDuotone color="orange" className="animate-pulse justify-self-center" />}
            </p>
        </a>
    </>);
}