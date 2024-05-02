import { ThermalPrinter } from "node-thermal-printer";

declare module "node-thermal-printer" {
    interface ThermalPrinter {
        printWithCustomLineBreaks(text: string): void;
    }
}

ThermalPrinter.prototype.printWithCustomLineBreaks = function (text: string) {
    // split at \n and print each line separately
    const lines = text.split("\n");
    lines.forEach((line) => {
        this.print(line);
        this.newLine();
    });
}

export default ThermalPrinter;