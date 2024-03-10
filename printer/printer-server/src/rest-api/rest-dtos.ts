/*
Terminology:
   ________________              _______________              _________
  |                |  n     1   |               |  n     n   |         |
  | Printer Server |  ------->  | Printer Queue |  ------->  | Printer |
  |________________|            |_______________|            |_________|
                                                  \ n
                                                   \
                                                    \    1    _______
                                                      ---->  |       |
                                                             |  Job  |
                                                             |_______|

    One printer can in theory be connected to multiple queues, however this is not advices and may 
    cause unknown behavior on print conflicts.
*/  

// GET  /v1/printer-server/{id}/print-queues
export interface PrintQueuesDto {
    queues: {
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
    }[]
}

// GET /v1/printer-server/{id}/print-queues/{id}/jobs/next
// or
// GET /v1/printer-server/{id}/print-queues/{id}/jobs/{id}
export interface PrintQueueJobDto {
    id: string,
    status: 'pending' | 'printing' | 'printed' | 'error',
    /** Am optional human readable message that describes the current status of the print job. For example an error message */
    statusMessage?: string,
    /** Base64 Encoded *png* image */
    base64PngLogoImage?: string,
    /** Printed at the top of the receipt */
    header?: string,
    /** Printed at the bottom of the receipt */
    footer?: string,
    /** String is converted to a QR code and printed onto the receipt */
    qrCode?: string,
    /** Printed in bold, centered at the top of the receipt */
    title?: string,
    /** The actual text that is printed onto the receipt. Use /n for new line */
    text: string,
}

// PUT /v1/printer-server/{id}/print-queues/{id}/jobs/{id}
export interface PrintQueueJobUpdateDto {
    status: 'pending' | 'printing' | 'printed' | 'error',
    statusMessage?: string,
}