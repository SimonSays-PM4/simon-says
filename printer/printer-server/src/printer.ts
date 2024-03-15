import { PrinterTypes, ThermalPrinter } from "node-thermal-printer";
import arp from "@network-utils/arp-lookup";
import { PrintQueueJobDto } from "printer-api/src/dtos";

export class Printer {
    readonly mac: string;
    readonly name: string;
    /** The printer is only initalised as soon as it is used for the first time. */
    thermalPrinter: ThermalPrinter | undefined;

    constructor(mac: string, name: string) {
        this.mac = mac;
        this.name = name;
    }

    /**
     * Initializes the printer and connects to it.
     * This method can be called multiple times. If the printer is already initialized it will do nothing.
     * @returns true if the printer was successfully initialized, false otherwise.
     */
    async init(): Promise<boolean> {
        if (this.thermalPrinter) {
            return true;
        }
        console.log(`Initializing printer ${this.name} with mac ${this.mac}`);
        const ip = await arp.toIP(this.mac);
        if (!ip) {
            console.error(`Failed to resolve printer ip for mac ${this.mac}`);
            return false;
        }
        console.log(`Resolved printer ip for mac ${this.mac} to ${ip}`);

        this.thermalPrinter = new ThermalPrinter({
            type: PrinterTypes.EPSON,
            interface: `tcp://${ip}`,
        });
        return true;
    }

    /**
     * Checks if the printer is connected.
     * @returns true if the printer is connected, false otherwise.
     */
    async isConnected(): Promise<boolean> {
        const initSuccessul = await this.init();
        if (!initSuccessul) {
            return false;
        }
        return await this.thermalPrinter!.isPrinterConnected();
    }

    /**
     * Prints the given print job.
     * @param printJob The print job to print.
     * @throws error if the printer is not connected or if the print job could not be printed.
     */
    async print(printJob: PrintQueueJobDto): Promise<void> {
        // Initialize the printer if it is not already initialized
        const initSuccessul = await this.init();
        if (!initSuccessul) {
            throw new Error(`Failed to initialize printer ${this.name} with mac ${this.mac}`);
        }

        // Check if the printer is connected
        const isConnected = await this.thermalPrinter!.isPrinterConnected();
        if (!isConnected) {
            throw new Error(`Failed to print job because printer ${this.name} with mac ${this.mac} is not connected`);
        }

        // Print the job
        // Print the logo if available
        if (printJob.base64PngLogoImage) {
            this.thermalPrinter!.alignCenter();
            const imageBuffer = Buffer.from(printJob.base64PngLogoImage, 'base64');
            await this.thermalPrinter!.printImageBuffer(imageBuffer);
            this.thermalPrinter!.alignLeft();
            this.thermalPrinter!.newLine();
        }

        // print header if available
        if (printJob.header) {
            this.thermalPrinter!.alignCenter();
            this.thermalPrinter!.println(printJob.header);
            this.thermalPrinter!.alignLeft();
        }

        // Print title if available
        if (printJob.title) {
            this.thermalPrinter!.alignCenter();
            this.thermalPrinter!.bold(true);
            this.thermalPrinter!.println(printJob.title);
            this.thermalPrinter!.bold(false);
            this.thermalPrinter!.alignLeft();
        }

        // Print body
        this.thermalPrinter!.println(printJob.body);

        // Print QR code if available
        if (printJob.qrCode) {
            this.thermalPrinter!.alignCenter();
            this.thermalPrinter!.newLine();
            this.thermalPrinter!.printQR(printJob.qrCode);
            this.thermalPrinter!.alignLeft();
        }

        // Print footer if available
        if (printJob.footer) {
            this.thermalPrinter!.newLine();
            this.thermalPrinter!.println(printJob.footer);
        }

        // Cut the paper and execute the print job
        this.thermalPrinter!.cut();
        try {
            await this.thermalPrinter!.execute({docname: printJob.id, waitForResponse: true});
        } catch (error) {
            throw new Error(`Failed to print job ${printJob.id} on printer ${this.name} with mac ${this.mac}: ${error}`);
        }
    }
}