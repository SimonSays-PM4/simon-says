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
    register: UseFormRegister<FieldValues>;
}

export const FormInput: React.FC<IFormInputProps> = ({
    classNames,
    id,
    label,
    type,
    defaultValue,
    placeholderValue,
    disabled = false,
    isRequired = false,
    validationError,
    minLength,
    maxLength,
    register
}) => {
    return (
        <div className={"sm:grid sm:grid-flow-row sm:grid-cols-1 sm:items-end my-4 " + (classNames ?? "")} key={id + defaultValue}>
            <label htmlFor={id} className="mb-2 block text-sm font-medium text-default-900">
                {label} {isRequired ? " *" : ""}
            </label>

            <div className="mt-1 sm:mt-0 sm:col-span-1 stroke-secondaryfont flex flex-row items-center">
                <div className="w-full relative">
                    <input
                        id={id}
                        className={
                            "form-input rounded-lg border border-default-200 px-4 py-2.5"
                        }
                        type={type}
                        disabled={disabled}
                        placeholder={placeholderValue}
                        aria-required={isRequired}
                        {...register(id, { required: isRequired, disabled: disabled, value: defaultValue, minLength: minLength, maxLength: maxLength })}
                    />
                </div>
            </div>

            <ValidationMessage validationError={validationError} />
        </div>
    );
};
