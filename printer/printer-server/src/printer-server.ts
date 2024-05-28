import { SocketApi } from 'printer-api-lib/src/socket-api';
import { ApplicationErrorDto, PrinterServerDto, PrintQueueDto } from 'printer-api-lib/src/dtos';
import { PrinterQueue } from './printer-queue';

export class PrinterServer {
    readonly printerServerId: string;
    readonly subscribedPrinterQueues: Map<string, PrinterQueue> = new Map();
    readonly printerQueuesSocketConnection: SocketApi<PrinterServerDto, PrinterServerDto>;

    constructor(printerServerId?: string) {
        this.printerServerId = printerServerId ?? process.env.PRINTER_SERVER_ID!;
        this.printerQueuesSocketConnection = SocketApi.connectToPrinterServer(
            process.env.PRINTER_QUEUE_SERVER_BASE_URL!,
            this.printerServerId,
            (printerServer) => this.onPrinterServerInitialData(printerServer),
            (printerServer) => this.onPrinterServerChange(printerServer),
            (printerServer) => this.onPrinterServerRemove(printerServer),
            (error) => this.onApplicationError(error),
            process.env.PRINTER_AUTH_TOKEN!
        );
    }

    onPrinterServerInitialData(printerServer: PrinterServerDto): void {
        console.log(`Initial data received for printer server "${printerServer.name}"`);
        // Disconnect from all queues and then remove them
        for (let oldQueue of this.subscribedPrinterQueues.values()) {
            oldQueue.disconnect();
        }
        this.subscribedPrinterQueues.clear();
        // Subscribe to all queues
        for (let printQueue of printerServer.queues) {
            const newQueue = new PrinterQueue(this.printerServerId, printQueue);
            this.subscribedPrinterQueues.set(printQueue.id, newQueue);
        }
    }

    onPrinterServerChange(printerServer: PrinterServerDto): void {
        console.log(`Change received for printer server ${printerServer.name}`);
        // disconnect from the old queue (if it exists) and overwrite it with the new one
        for (let printQueue of printerServer.queues) {
            this.onPrinterQueueChange(printQueue);
        }
    }

    onPrinterQueueChange(printQueue: PrintQueueDto): void {
        const oldQueue = this.subscribedPrinterQueues.get(printQueue.id);
        if (oldQueue) {
            oldQueue.disconnect();
        }
        const newQueue = new PrinterQueue(this.printerServerId, printQueue);
        this.subscribedPrinterQueues.set(printQueue.id, newQueue);
    }

    onPrinterServerRemove(printerServer: PrinterServerDto): void {
        console.log(`Remove received for printer server ${printerServer.name}`);
        for (let printQueue of printerServer.queues) {
            this.onPrinterQueueRemove(printQueue);
        }
    }

    onPrinterQueueRemove(printQueue: PrintQueueDto): void {
        const oldQueue = this.subscribedPrinterQueues.get(printQueue.id);
        if (oldQueue) {
            oldQueue.disconnect();
        }
        this.subscribedPrinterQueues.delete(printQueue.id);
    }

    onApplicationError(error: ApplicationErrorDto): void {
        console.error("Error in printer server socket connection", error);
    }

    /**
     * Disconnect all queues and the printer server socket
     */
    disconnect(): void {
        this.printerQueuesSocketConnection.disconnect();
        for (let queue of this.subscribedPrinterQueues.values()) {
            queue.disconnect();
        }
    }
}