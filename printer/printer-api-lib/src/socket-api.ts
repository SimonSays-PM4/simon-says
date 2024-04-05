import { io, Socket } from 'socket.io-client';
import { ApplicationErrorDto, PrinterServerDto, PrinterServersDto, PrintQueueDto, PrintQueueJobDto, PrintQueueJobsDto } from './dtos';

const defaultMaxAckTimeout = 2000;

export class SocketApi<OnConnectType, OnChangeType> {
    readonly socketUrl: string;
    readonly onInitialData: (data: OnConnectType) => void;
    readonly onChange: (data: OnChangeType) => void;
    readonly onApplicationError: (error: ApplicationErrorDto) => void;
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
        onRemove: (data: OnChangeType) => void,
        onApplicationError: (error: ApplicationErrorDto) => void
    ) {
        console.debug(`Connecting to socket at ${socketUrl}`);
        this.socketUrl = socketUrl;
        this.onInitialData = onInitialData;
        this.onChange = onChange;
        this.onApplicationError = onApplicationError;
        this.socket = io(socketUrl).timeout(defaultMaxAckTimeout);
        this.socket.on('initial-data', onInitialData);
        this.socket.on('change', onChange);
        this.socket.on('remove', onRemove);
        this.socket.on('application-error', onApplicationError);
        this.socket.on('connect_error', (error: Error) => {
            console.error(`🧦 Socket connection error: ${error.message}`);
        });

        // For debugging
        this.socket.onAny((event, ...args) => {
            console.debug(`🧦 Socket event: ${event}`, args);
        });
    }

    async sendChange(data: Partial<OnChangeType>): Promise<void> {
        await this.socket.emitWithAck('change', data);
    }

    async sendRemove<T>(data: Partial<OnChangeType>): Promise<void> {
        await this.socket.emitWithAck('remove', data);
    }

    // connect to /socket-api/v1/printer-servers
    static connectToPrinterServers(
        printerQueueServerBaseUrl: string,
        onInitialData: (data: PrinterServersDto) => void,
        onChange: (data: PrinterServerDto) => void,
        onRemove: (data: PrinterServerDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void
    ): SocketApi<PrinterServersDto, PrinterServerDto> {
        return new SocketApi<PrinterServersDto, PrinterServerDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers`, onInitialData, onChange, onRemove, onApplicationError);
    }

    // connect to /socket-api/v1/printer-servers/{id}
    static connectToPrinterServer(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        onInitialData: (data: PrinterServerDto) => void,
        onChange: (data: PrinterServerDto) => void,
        onRemove: (data: PrinterServerDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void
    ): SocketApi<PrinterServerDto, PrinterServerDto> {
        return new SocketApi<PrinterServerDto, PrinterServerDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}`, onInitialData, onChange, onRemove, onApplicationError);
    }

    // connect to /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs
    static connectToPrintQueueJobs(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        onInitialData: (data: PrintQueueJobsDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void
    ): SocketApi<PrintQueueJobsDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobsDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}/print-queues/${queueId}/jobs`, onInitialData, onChange, onRemove, onApplicationError);
    }

    // connect to /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs/{id}
    static connectToPrintQueueJob(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        jobId: string,
        onInitialData: (data: PrintQueueJobDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void
    ): SocketApi<PrintQueueJobDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}/print-queues/${queueId}/jobs/${jobId}`, onInitialData, onChange, onRemove, onApplicationError);
    }

    // connect to /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs/next
    static connectToPrintQueueNextJob(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        onInitialData: (data: PrintQueueJobDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void
    ): SocketApi<PrintQueueJobDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}/print-queues/${queueId}/jobs/next`, onInitialData, onChange, onRemove, onApplicationError);
    }

    /**
     * Disconnects the socket
     */
    disconnect() {
        this.socket.disconnect();
    }
}