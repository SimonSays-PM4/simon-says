/*
Terminology:
   ________________              _______________              _________
  |                |  1     n   |               |  n     n   |         |
  | Printer Server |  ------->  | Printer Queue |  ------->  | Printer |
  |________________|            |_______________|            |_________|
                                                  \ 1
                                                   \
                                                    \    n    _______
                                                      ---->  |       |
                                                             |  Job  |
                                                             |_______|

    One printer can in theory be connected to multiple queues, however this is not advices and may 
    cause unknown behavior on simultaneous prints.
*/

/**
 * /socket-api/v1/printer-servers
 */
export type PrinterServersDto = PrinterServerDto[]

/**
 * /socket-api/v1/printer-servers/{id}
 */
export interface PrinterServerDto {
    id: string,
    /** A human readable name mainly used for human debugging ;) */
    name: string,
    /** All queues that are connected to this printer server */
    queues: PrintQueueDto[],
}

export interface PrintQueueDto {
    id: string,
    /** A human readable name mainly used for human debugging ;) */
    name: string,
    /**  
     * The printers that are connected to this queue. One queue can have multiple printers connected to it.
     * The print server will always attempt to use the first printer in this list. If for any reason the first
     * printer is not reachable (network issues, errors, ...) the print server will fall back on the next in the list.
     * 
     * One printer can in theory be connected to multiple queues, however this is not advices and may cause unknown behavior
     * on print conflicts.
     */
    printers: {
        /** A human readable name mainly used for human debugging ;) */
        name: string,
        /** The mac adress of the printer. This is to be considered as the unique id of the printer.*/
        mac: string
    }[],
}

/**
 * To subscribe to changes in the next pending job use the following socket.io path
 * /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs/next
 * The backend will always emit the current next pending job on connection and then
 * everytime it changes.
 * 
 * You may also subscribe to a specific job by using the job id
 * /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs/{id}
 * The backend will on connect emit the current state of the job and then everytime it changes.
 * 
 * The print job is printed in the following format:
 * 
 *      ---------------------------
 *      |                         |
 *      |      <logo/image>       |
 *      |                         |
 *      |        <header>         |
 *      |                         |
 *      |        <*title*>        |
 *      |                         |
 *      | <body>                  |
 *      |                         |
 *      |        <QR-code>        |
 *      |                         |
 *      |        <footer>         |
 *      |                         |
 *      --------------------------- 
 */
export interface PrintQueueJobDto {
    id: string,
    /** The status of the print. 'printing' was deliberately left out as a state since the printer prints so fast that it is not worth the effort to track it and it would only complicate the system. */
    status: 'PENDING' | 'PRINTED' | 'ERROR' | 'CANCELED',
    /** Am optional human readable message that describes the current status of the print job. For example an error message */
    statusMessage?: string,
    /** Base64 Encoded *png* image printed at the top of the receipt */
    base64PngLogoImage?: string,
    /** Printed centered at the top of the receipt */
    header?: string,
    /** Printed in bold, centered below the header and above the body of the receipt */
    title?: string,
    /** The actual text that is printed onto the receipt. Use /n for new line */
    body: string,
    /** A qr code below the body but above the footer. The test will be converted to a qr code. Also works for links */
    qrCode?: string,
    /** Printed centered at the bottom of the receipt */
    footer?: string,
    /** The creation datetime of the print job (in ms since epoch) */
    creationDateTime: number,
    /** The last time the job was updated (in ms since epoch) */
    lastUpdateDateTime: number,
}

/**
 * Get all jobs since ever and subscribe to all job changes
 * /socket-api/v1/printer-servers/{id}/print-queues/{id}/jobs
 * The backend will emit all jobs on connect and then everytime a job changes.
 */
export type PrintQueueJobsDto = PrintQueueJobDto[]

/**
 * Used as socket.io event to update the status of a print job
*/
 export interface PrintQueueJobUpdateDto {
    id: string,
    /** The status of the print */
    status: 'PENDING' | 'PRINTED' | 'ERROR' | 'CANCELED',
    /** An optional human readable message that describes the current status of the print job. For example an error message */
    statusMessage?: string,
}

/**
 * A generic error response
 */
export interface ApplicationErrorDto {
    code: string,
    message: string,
}