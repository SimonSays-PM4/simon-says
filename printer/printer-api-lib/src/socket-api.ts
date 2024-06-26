import { io, Socket } from 'socket.io-client';
import { ApplicationErrorDto, PrinterServerDto, PrinterServersDto, PrintQueueJobDto, PrintQueueJobsDto } from './dtos';

export class SocketApi<OnConnectType, OnChangeType> {
    readonly socketUrl: string;
    readonly onInitialData: (data: OnConnectType) => void;
    readonly onChange: (data: OnChangeType) => void;
    readonly onApplicationError: (error: ApplicationErrorDto) => void;
    readonly socket: Socket;
    readonly authorizationHeader: string | undefined = undefined;

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
        onApplicationError: (error: ApplicationErrorDto) => void,
        authorizationHeader: string
    ) {
        console.debug(`Connecting to socket at ${socketUrl}`);
        this.socketUrl = socketUrl;
        this.onInitialData = onInitialData;
        this.onChange = onChange;
        this.onApplicationError = onApplicationError;
        this.authorizationHeader = authorizationHeader;
        this.socket = io(socketUrl, { 
            autoConnect: false, 
            extraHeaders: {
                "Authorization": this.authorizationHeader
            },
            transports: ['polling']
        });
        this.socket.on('initial-data', onInitialData);
        this.socket.on('change', onChange);
        this.socket.on('remove', onRemove);
        this.socket.on('application-error', onApplicationError);

        // For debugging
        this.socket.on('error', (error: any) => console.error(`🧦 Socket error:`, error));
        this.socket.on('reconnect', (attemptNumber: number) => console.debug(`🧦 Socket reconnected after ${attemptNumber} attempts`));
        this.socket.on('reconnect_attempt', (attemptNumber: number) => console.debug(`🧦 Socket reconnect attempt ${attemptNumber}`));
        this.socket.on('reconnect_error', (error: any) => console.error(`🧦 Socket reconnect error:`, error))
        this.socket.on('reconnect_failed', () => console.error(`🧦 Socket reconnect failed`));
        this.socket.on('connect', () => console.debug(`🧦 Socket connected to ${socketUrl}`));
        this.socket.on('connect_error', (error: any) => console.error(`🧦 Socket connection error:`, error));
        this.socket.on('disconnect', () => console.debug(`🧦 Socket disconnected from ${socketUrl}`));

        // For debugging event more
        this.socket.onAny((event, ...args) => {
            console.debug(`🧦 Socket event: ${event}`, args);
        });

        // connect to the socket
        this.socket = this.socket.connect();
    }

    async sendChange(data: Partial<OnChangeType>): Promise<void> {
        console.debug('Sending change to %s', this.socketUrl, data);
        await this.socket.emit('change', data);
    }

    async sendRemove(data: Partial<OnChangeType>): Promise<void> {
        console.debug('Sending remove to %s', this.socketUrl, data);
        await this.socket.emit('remove', data);
    }

    // connect to /socket-api/v1/printer-servers
    static connectToPrinterServers(
        printerQueueServerBaseUrl: string,
        onInitialData: (data: PrinterServersDto) => void,
        onChange: (data: PrinterServerDto) => void,
        onRemove: (data: PrinterServerDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void,
        authorizationHeader: string
    ): SocketApi<PrinterServersDto, PrinterServerDto> {
        return new SocketApi<PrinterServersDto, PrinterServerDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers`, onInitialData, onChange, onRemove, onApplicationError, authorizationHeader);
    }

    // connect to /socket-api/v1/printer-servers/{id}
    static connectToPrinterServer(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        onInitialData: (data: PrinterServerDto) => void,
        onChange: (data: PrinterServerDto) => void,
        onRemove: (data: PrinterServerDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void,
        authorizationHeader: string
    ): SocketApi<PrinterServerDto, PrinterServerDto> {
        return new SocketApi<PrinterServerDto, PrinterServerDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}`, onInitialData, onChange, onRemove, onApplicationError, authorizationHeader);
    }

    // connect to /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs
    static connectToPrintQueueJobs(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        onInitialData: (data: PrintQueueJobsDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void,
        authorizationHeader: string
    ): SocketApi<PrintQueueJobsDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobsDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}/print-queues/${queueId}/jobs`, onInitialData, onChange, onRemove, onApplicationError, authorizationHeader);
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
        onApplicationError: (error: ApplicationErrorDto) => void,
        authorizationHeader: string
    ): SocketApi<PrintQueueJobDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}/print-queues/${queueId}/jobs/${jobId}`, onInitialData, onChange, onRemove, onApplicationError, authorizationHeader);
    }

    // connect to /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs/next
    static connectToPrintQueueNextJob(
        printerQueueServerBaseUrl: string,
        printerServerId: string,
        queueId: string,
        onInitialData: (data: PrintQueueJobDto) => void,
        onChange: (data: PrintQueueJobDto) => void,
        onRemove: (data: PrintQueueJobDto) => void,
        onApplicationError: (error: ApplicationErrorDto) => void,
        authorizationHeader: string
    ): SocketApi<PrintQueueJobDto, PrintQueueJobDto> {
        return new SocketApi<PrintQueueJobDto, PrintQueueJobDto>(`${printerQueueServerBaseUrl}/socket-api/v1/printer-servers/${printerServerId}/print-queues/${queueId}/jobs/next`, onInitialData, onChange, onRemove, onApplicationError, authorizationHeader);
    }

    /**
     * Disconnects the socket
     */
    disconnect() {
        this.socket.disconnect();
    }
}