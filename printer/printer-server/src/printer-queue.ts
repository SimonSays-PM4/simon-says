import { SocketApi } from 'printer-api-lib/src/socket-api';
import { ApplicationErrorDto, PrintQueueDto, PrintQueueJobDto, PrintQueueJobUpdateDto } from 'printer-api-lib/src/dtos';
import { Printer } from "./printer";
import { error } from 'console';

export class PrinterQueue {
    readonly printServerId: string;
    readonly printQueueDto: PrintQueueDto;
    readonly nextPrintQueueJobConnection: SocketApi<PrintQueueJobDto, PrintQueueJobDto>;
    readonly printers: Printer[] = [];

    constructor(printServerId: string, printQueueDto: PrintQueueDto) {
        this.printServerId = printServerId;
        this.printQueueDto = printQueueDto;
        for (let printer of printQueueDto.printers) {
            this.printers.push(new Printer(printer.mac, printer.name));
        }
        this.nextPrintQueueJobConnection = SocketApi.connectToPrintQueueNextJob(
            process.env.PRINTER_QUEUE_SERVER_BASE_URL!,
            this.printServerId,
            this.printQueueDto.id,
            (printQueueJob) => this.onPrintQueueJobInitialData(printQueueJob),
            (printQueueJob) => this.onPrintQueueJobChange(printQueueJob),
            (printQueueJob) => this.onPrintQueueJobRemove(printQueueJob),
            (error) => this.onApplicationError(error)
        );
    }

    disconnect(): void {
        this.nextPrintQueueJobConnection.disconnect();
    }

    onPrintQueueJobInitialData(printQueueJob: PrintQueueJobDto): void {
        console.log(`Initial data received for print job ${printQueueJob.id}`);
        this.print(printQueueJob);
    }

    onPrintQueueJobChange(printQueueJob: PrintQueueJobDto): void {
        console.log(`Change received for print job ${printQueueJob.id}`);
        this.print(printQueueJob);
    }

    onPrintQueueJobRemove(printQueueJob: PrintQueueJobDto): void {
        // Do nothing
        console.warn(`Print job ${printQueueJob.id} was removed from the queue. The print server does not support removing/stopping print jobs.`);
    }

    onApplicationError(error: ApplicationErrorDto): void {
        console.error("Error in printer queue socket connection", error);
    }

    async print(printJob: PrintQueueJobDto): Promise<void> {
        if(printJob.status !== "PENDING") {
            console.log(`Print job ${printJob.id} is not pending. Nothing to print here.`);
            return;
        }

        // Collect all errors that occur during printing
        const printErrors = [];

        // We attempt to print the job on each printer in the queue until we find one that works
        for (let printer of this.printers) {
            if (await printer.isConnected()) {
                // Attempt to print the job
                try {
                    await printer.print(printJob);
                    await this.updatePrintQueueJob({
                        id: printJob.id,
                        status: "PRINTED"
                    });
                    return;
                } catch (error) {
                    // Handle the error and continue with the next printer
                    console.error(`Failed to print job ${printJob.id} on printer ${printer.name} with mac ${printer.mac}: ${error}`);
                    printErrors.push(error);
                    const isLastPrinter = printer === this.printers[this.printers.length - 1];
                    if (!isLastPrinter) {
                        console.log(`Trying to print job ${printJob.id} on the next available printer`);
                    }
                }
            }
        }

        // If we reach this point, no printer was able to print the job
        var errorMessage = `Failed to print job ${printJob.id} because no printer was able to print the job.`;
        console.error(errorMessage, printErrors);
        await this.updatePrintQueueJob({
            id: printJob.id,
            status: "ERROR",
            statusMessage: errorMessage
        });
    }

    async updatePrintQueueJob(update: PrintQueueJobUpdateDto): Promise<void> {
        try {
            await this.nextPrintQueueJobConnection.sendChange(update);
        } catch (error) {
            // try again after a short delay
            console.log("Failed to send update of printer job. Retrying in 1 seconds.", error);
            setTimeout(async () => {
                await this.updatePrintQueueJob(update);
            }, 1);
            console.error("An error occured while sending update of printer job", error) 
        }
    }
}