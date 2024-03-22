import * as React from "react";
import { FieldValues, UseFormRegister } from "react-hook-form";

export interface ITextInputProps {
    id: string;
    defaultValue?: string;
    placeholderValue?: string;
    label: string;
    classNames?: string;
    disabled?: boolean;
    isRequired?: boolean;
    register: UseFormRegister<FieldValues>;
}

export const TextInput: React.FC<ITextInputProps> = ({
    classNames,
    id,
    label,
    defaultValue,
    placeholderValue,
    disabled = false,
    isRequired = false,
    register
}) => {
    return (
        <div className={"sm:grid sm:grid-flow-row sm:grid-cols-1 sm:items-end " + (classNames ?? "")} key={id + defaultValue}>
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
                        disabled={disabled}
                        placeholder={placeholderValue}
                        aria-required={isRequired}
                        {...register(id, { required: isRequired, disabled: disabled, value: defaultValue })}
                    />
                </div>
            </div>
        </div>
    );
};
