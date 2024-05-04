import * as React from "react";
import { Transition, Dialog as HeadlessUiDialog } from "@headlessui/react";
import { Button } from "./Button";
import { ButtonType } from "../enums/ButtonType";

export interface IDialogProps {
    title: string;
    cancelText: string;
    okText: string;
    isOpen: boolean;
    setIsOpen: (state: boolean) => void;
    cancelAction: () => void;
    okAction: () => void;
    children: React.ReactNode;
}

export const Dialog: React.FC<IDialogProps> = ({
    okText,
    isOpen,
    setIsOpen,
    title,
    cancelText,
    children,
    okAction,
    cancelAction,
}) => {
    return (
        <Transition.Root show={isOpen} as={React.Fragment}>
            <HeadlessUiDialog as="div" className="relative z-10" onClose={setIsOpen}>
                <Transition.Child
                    as={React.Fragment}
                    enter="ease-out duration-300"
                    enterFrom="opacity-0"
                    enterTo="opacity-100"
                    leave="ease-in duration-200"
                    leaveFrom="opacity-100"
                    leaveTo="opacity-0"
                >
                    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
                </Transition.Child>

                <div className="fixed inset-0 z-10 overflow-y-auto">
                    <div className="text-center p-4 bg-black bg-opacity-10 min-h-full flex sm:justify-center items-center">
                        <Transition.Child
                            as={React.Fragment}
                            enter="ease-out duration-300"
                            enterFrom="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
                            enterTo="opacity-100 translate-y-0 sm:scale-100"
                            leave="ease-in duration-200"
                            leaveFrom="opacity-100 translate-y-0 sm:scale-100"
                            leaveTo="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
                        >
                            <HeadlessUiDialog.Panel className="relative transform rounded-3xl bg-white px-6 pt-7 pb-6 text-left w-full shadow-popup transition-all sm:my-8 max-w-lg sm:max-w-xl md:max-w-2xl lg:max-w-4xl sm:px-20 sm:py-16">
                                <div className="mt-3 sm:mt-5">
                                    <HeadlessUiDialog.Title
                                        as="h2"
                                        className="text-xl sm:text-2xl font-medium leading-6 text-gray-900 pb-8 mt-[-15px]"
                                    >
                                        {title}
                                    </HeadlessUiDialog.Title>
                                    <div className="space-y-6 sm:space-y-5">{children}</div>
                                </div>

                                <div className="mt-8 grid gap-2 sm:grid-flow-row-dense sm:grid-cols-2 sm:gap-3">
                                    <Button
                                        buttonType={ButtonType.Primary}
                                        buttonText={okText}
                                        className="inline-flex w-full px-4 py-2 sm:col-start-2 text-base font-medium bg-primary flex-col items-center rounded-lg text-white justify-center hover:bg-hoverprimar hover:shadow-sm hover:shadow-shadow min-h-[48px]  focus:shadow-lg focus:outline focus:outline-[3px] focus:outline-white focus:border-[#B7B7B7] border border-primary"
                                        onClick={() => {
                                            if (okAction) {
                                                okAction();
                                            }
                                        }}
                                    />
                                    <Button
                                        buttonType={ButtonType.Secondary}
                                        buttonText={cancelText}
                                        className="inline-flex w-full justify-center rounded-lg px-4 py-2 bg-secondary text-secondaryfont hover:bg-hoversecondary stroke-secondaryfont focus:shadow-lg focus:outline focus:outline-[3px] focus:outline-white focus:border-[#B7B7B7] border border-secondary"
                                        onClick={() => {
                                            if (cancelAction) {
                                                cancelAction();
                                            }

                                            setIsOpen(false);
                                        }}
                                    />
                                </div>
                            </HeadlessUiDialog.Panel>
                        </Transition.Child>
                    </div>
                </div>
            </HeadlessUiDialog>
        </Transition.Root>
    );
};
