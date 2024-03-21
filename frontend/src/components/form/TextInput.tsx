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
            <label htmlFor={id} className="block text-sm font-medium text-gray-700 sm:mt-px pt-2 mb-2">
                {label} {isRequired ? " *" : ""}
            </label>

            <div className="mt-1 sm:mt-0 sm:col-span-1 stroke-secondaryfont flex flex-row items-center">
                <div className="w-full relative">
                    <input
                        id={id}
                        className={
                            "w-full h-12 tracking-[0.4px] leading-[18px] sm:leading-[18px] shadow-sm bg-[#F6F6F6] border-none hover:border-none focus:border-none sm:text-sm rounded-md pl-3 pr-10 disabled:opacity-60 opacity-100 disabled:ring-0"
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
