import { PrintQueueJobDto, PrintQueueJobUpdateDto, PrintQueuesDto } from "./rest-dtos";

export class RestApi {
    readonly baseUrl: string;
    readonly printServerId: string;

    constructor() {
        this.baseUrl = process.env.PRINTER_QUEUE_SERVER_BASE_URL!;
        this.printServerId = process.env.PRINTER_QUEUE_SERVER_ID!;
    }

    // GET  /v1/printer-server/{id}/print-queues
    async getPrintQueues(): Promise<PrintQueuesDto> {
        const response = await this.loggedFetch(`${this.baseUrl}/v1/printer-server/${this.printServerId}/print-queues`);
        return await response.json() as PrintQueuesDto;
    }

    // GET /v1/printer-server/{id}/print-queues/{id}/jobs/next
    // or
    // GET /v1/printer-server/{id}/print-queues/{id}/jobs/{id}
    async getPrintQueueJob(queueId: string, jobId: string | 'next'): Promise<PrintQueueJobDto> {
        const response = await this.loggedFetch(`${this.baseUrl}/v1/printer-server/${this.printServerId}/print-queues/${queueId}/jobs/${jobId}`);
        return await response.json() as PrintQueueJobDto;
    }

    // PUT /v1/printer-server/{id}/print-queues/{id}/jobs/{id}
    async updatePrintQueueJob(queueId: string, jobId: string, jobUpdate: PrintQueueJobUpdateDto): Promise<void> {
        await this.loggedFetch(`${this.baseUrl}/v1/printer-server/${this.printServerId}/print-queues/${queueId}/jobs/${jobId}`, {
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