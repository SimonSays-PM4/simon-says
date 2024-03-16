import { io, Socket } from 'socket.io-client';
import { PrintQueueDto, PrintQueueJobDto, PrintQueueJobsDto, PrintQueuesDto } from './dtos';

const defaultMaxAckTimeout = 1000;

export class SocketApi<OnConnectType, OnChangeType> {
    readonly socketUrl: string;
    readonly onInitialData: (data: OnConnectType) => void;
    readonly onChange: (data: OnChangeType) => void;
    readonly socket: Socket;

    get isConnected(): boolean {
        return this.socket.connected;
    }

    /**
     * A generic implementatiaon of a socket.io client for the printer server usecase
     * @param socketUrl The url to connect to
     * @param onInitialData The callback to call when the initial data is received. This event may be emitted multiple times in case of reconnects.
     * @param onChange The callback to call when the data changes. This event can be emitted many times.
     */
    constructor(
        socketUrl: string,
        onInitialData: (data: OnConnectType) => void,
        onChange: (data: OnChangeType) => void,
        onRemove: (data: OnChangeType) => void
    ) {
        this.socketUrl = socketUrl;
        this.onInitialData = onInitialData;
        this.onChange = onChange;
        this.socket = io(socketUrl).timeout(defaultMaxAckTimeout);
        this.socket.on('initial-data', onInitialData);
        this.socket.on('change', onChange);
        this.socket.on('remove', onRemove);

        // For debugging
        this.socket.onAny((event, ...args) => {
            console.debug(`🧦 Socket event: ${event}`, args);
        });
    }

    async sendChange<T>(data: T): Promise<void> {
        await this.socket.emitWithAck('change', data);
    }

    async sendRemove<T>(data: T): Promise<void> {
        await this.socket.emitWithAck('remove', data);
    }

    // connect to /socket-api/v1/printer-server/{id}/print-queues
    static connectToPrintQueues(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        onInitialData: (data: PrintQueuesDto) => void,
        onChange: (data: PrintQueueDto) => void,
        onRemove: (data: PrintQueueDto) => void
    ): SocketApi<PrintQueuesDto, PrintQueueDto> {
        return new SocketApi<PrintQueuesDto, PrintQueueDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-server/${printerServerId}/print-queues`, onInitialData, onChange, onRemove);
    }

    // connect to /socket-api/v1/printer-server/{id}/print-queues/{id}/jobs
    static connectToPrintQueueJobs(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        onInitialData: (data: PrintQueueJobsDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void
    ): SocketApi<PrintQueueJobsDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobsDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-server/${printerServerId}/print-queues/${queueId}/jobs`, onInitialData, onChange, onRemove);
    }

    // connect to /socket-api/v1/printer-server/{id}/print-queues/{id}/jobs/{id}
    static connectToPrintQueueJob(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        jobId: string,
        onInitialData: (data: PrintQueueJobDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void
    ): SocketApi<PrintQueueJobDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-server/${printerServerId}/print-queues/${queueId}/jobs/${jobId}`, onInitialData, onChange, onRemove);
    }

    // connect to /socket-api/v1/printer-server/{id}/print-queues/{id}/jobs/next
    static connectToPrintQueueNextJob(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        onInitialData: (data: PrintQueueJobDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void
    ): SocketApi<PrintQueueJobDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-server/${printerServerId}/print-queues/${queueId}/jobs/next`, onInitialData, onChange, onRemove);
    }

    /**
     * Disconnects the socket
     */
    disconnect() {
        this.socket.disconnect();
    }
}