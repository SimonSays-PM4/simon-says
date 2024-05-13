import * as React from "react";
import { FieldValues, UseFormRegister } from "react-hook-form";
import { ValidationMessage } from "./ValidationMessage";

export interface IFormInputProps {
    id: string;
    defaultValue?: string;
    placeholderValue?: string;
    label: string;
    type: React.HTMLInputTypeAttribute;
    classNames?: string;
    disabled?: boolean;
    isRequired?: boolean;
    validationError?: string;
    minLength?: number;
    maxLength?: number;
    min?: number;
    max?: number;
    step?: number;
    register: UseFormRegister<FieldValues>;
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export const FormInput: React.FC<IFormInputProps> = ({
    id,
    label,
    type,
    classNames,
    defaultValue,
    placeholderValue,
    disabled = false,
    isRequired = false,
    validationError,
    minLength,
    maxLength,
    min,
    max,
    step = undefined,
    register,
    onChange
}) => {
    return (
        <div className={"sm:grid sm:grid-flow-row sm:grid-cols-1 sm:items-end my-4 " + (classNames ?? "")} key={id + defaultValue}>
            <label htmlFor={id} className="mb-2 block text-sm font-medium text-default-900">
                {label} {isRequired ? " *" : ""}
            </label>

            <div className="mt-1 sm:mt-0 sm:col-span-1 stroke-secondaryfont flex flex-row items-center">
                <div className="w-full relative">
                    <input
                        key={id}
                        id={id}
                        className={
                            "form-input rounded-lg border border-default-200 px-4 py-2.5"
                        }
                        {...register(id, { required: isRequired, disabled: disabled, value: defaultValue, minLength: minLength, maxLength: maxLength, min: min, max: max })}
                        onChange={onChange}
                        type={type}
                        disabled={disabled}
                        placeholder={placeholderValue}
                        aria-required={isRequired}
                        step={step}
                    />
                </div>
            </div>

            <ValidationMessage validationError={validationError} />
        </div>
    );
};
