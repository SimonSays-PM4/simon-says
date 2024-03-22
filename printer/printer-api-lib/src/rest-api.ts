import { PrintQueueJobDto, PrintQueueJobUpdateDto, PrintQueuesDto } from "./dtos";

export class RestApi {
    readonly printerQueueServerBaseUrl: string;
    readonly printerServerId: string;

    constructor(printerQueueServerBaseUrl: string, printerServerId: string) {
        this.printerQueueServerBaseUrl = printerQueueServerBaseUrl;
        this.printerServerId = printerServerId;
    }

    // GET  /v1/printer-server/{id}/print-queues
    async getPrintQueues(): Promise<PrintQueuesDto> {
        const response = await this.loggedFetch(`${this.printerQueueServerBaseUrl}/rest-api/v1/printer-server/${this.printerServerId}/print-queues`);
        return await response.json() as PrintQueuesDto;
    }

    // GET /v1/printer-server/{id}/print-queues/{id}/jobs/next
    // or
    // GET /v1/printer-server/{id}/print-queues/{id}/jobs/{id}
    async getPrintQueueJob(queueId: string, jobId: string | 'next'): Promise<PrintQueueJobDto> {
        const response = await this.loggedFetch(`${this.printerQueueServerBaseUrl}/rest-api/v1/printer-server/${this.printerServerId}/print-queues/${queueId}/jobs/${jobId}`);
        return await response.json() as PrintQueueJobDto;
    }

    // GET /v1/printer-server/{id}/print-queues/{id}/jobs
    async getPrintQueueJobs(queueId: string): Promise<PrintQueueJobDto[]> {
        const response = await this.loggedFetch(`${this.printerQueueServerBaseUrl}/rest-api/v1/printer-server/${this.printerServerId}/print-queues/${queueId}/jobs`);
        return await response.json() as PrintQueueJobDto[];
    }

    // PUT /v1/printer-server/{id}/print-queues/{id}/jobs/{id}
    async updatePrintQueueJob(queueId: string, jobId: string, jobUpdate: PrintQueueJobUpdateDto): Promise<void> {
        await this.loggedFetch(`${this.printerQueueServerBaseUrl}/rest-api/v1/printer-server/${this.printerServerId}/print-queues/${queueId}/jobs/${jobId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jobUpdate)
        });
    }

    /**
     * Same as fetch but logs the request and response
     * @param url The url to fetch
     * @param options The fetch options
     * @returns The response
     */
    async loggedFetch(url: string, options?: RequestInit): Promise<Response> {
        console.log(`-> ${options?.method ?? "GET"} ${url}`);
        const response = await fetch(url, options);
        if (!response.ok) {
            console.error(`<- ${options?.method ?? "GET"} [${response.status}] ${url}: ${response.statusText}`);
            console.error(await response.text());
        } else {
            console.log(`<- ${options?.method ?? "GET"} [${response.status}] ${url}`);
        }
        return response;
    }
}