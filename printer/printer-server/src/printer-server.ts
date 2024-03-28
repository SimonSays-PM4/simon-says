import { SocketApi } from 'printer-api-lib/src/socket-api';
import { PrintQueueDto, PrintQueuesDto } from 'printer-api-lib/src/dtos';
import { PrinterQueue } from './printer-queue';

export class PrinterServer {
    readonly printerServerId: string;
    readonly printerQueuesSocketConnection: SocketApi<PrintQueuesDto, PrintQueueDto>;
    subscribedPrinterQueues: Map<string, PrinterQueue> = new Map();

    constructor(printerServerId?: string) {
        this.printerServerId = printerServerId ?? process.env.PRINTER_SERVER_ID!;
        this.printerQueuesSocketConnection = SocketApi.connectToPrintQueues(
            process.env.PRINTER_QUEUE_SERVER_BASE_URL!,
            this.printerServerId,
            this.onPrintQueuesInitialData,
            this.onPrintQueueChange,
            this.onPrintQueueRemove
        );
    }

    onPrintQueuesInitialData(printQueues: PrintQueuesDto): void {
        // Disconnect from all queues and then remove them
        for (let oldQueue of this.subscribedPrinterQueues.values()) {
            oldQueue.disconnect();
        }
        this.subscribedPrinterQueues.clear();
        // Subscribe to all queues
        for (let printQueue of printQueues) {
            const newQueue = new PrinterQueue(this.printerServerId, printQueue);
            this.subscribedPrinterQueues.set(printQueue.id, newQueue);
        }
    }

    onPrintQueueChange(printQueue: PrintQueueDto): void {
        // disconnect from the old queue (if it exists) and overwrite it with the new one
        const oldQueue = this.subscribedPrinterQueues.get(printQueue.id);
        if (oldQueue) {
            oldQueue.disconnect();
        }
        const newQueue = new PrinterQueue(this.printerServerId, printQueue);
        this.subscribedPrinterQueues.set(printQueue.id, newQueue);
    }

    onPrintQueueRemove(printQueue: PrintQueueDto): void {
        const oldQueue = this.subscribedPrinterQueues.get(printQueue.id);
        if (oldQueue) {
            oldQueue.disconnect();
        }
        this.subscribedPrinterQueues.delete(printQueue.id);
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