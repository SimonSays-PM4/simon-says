import * as React from "react";

export interface ValidationMessageProps {
    validationError?: string;
}

export const ValidationMessage: React.FC<ValidationMessageProps> = ({ validationError }) => {
    return (
        <>
            {validationError && (
                <p className="w-full h-full sm:pt-2 text-primary">{validationError}</p>
            )}
        </>
    );
};
