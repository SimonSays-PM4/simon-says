import { PrinterTypes, ThermalPrinter } from "node-thermal-printer";
import arp from "@network-utils/arp-lookup";
import { PrintQueueJobDto } from "printer-api-lib/src/dtos";
import os from "os";
import Arpping from "arpping";

const networkScanMaxTimeoutInS = 10;

export class Printer {
    readonly mac: string;
    readonly name: string;
    /** The printer is only initalised as soon as it is used for the first time. */
    thermalPrinter: ThermalPrinter | undefined;

    constructor(mac: string, name: string) {
        this.mac = mac;
        this.name = name;
    }

    /**
     * Initializes the printer and connects to it.
     * This method can be called multiple times. If the printer is already initialized it will do nothing.
     * @returns true if the printer was successfully initialized, false otherwise.
     */
    async init(): Promise<boolean> {
        // If already initialized return true or if the printer is in dry run mode
        if (this.thermalPrinter || process.env.DRY_RUN_PRINTER_MAC_ADDRESS === this.mac) {
            return true;
        }
        console.log(`Initializing printer ${this.name} with mac ${this.mac}`);
        // First we try to find the ip address of the printer in our arp cache because this is the fastest way
        let ip = await arp.toIP(this.mac);
        if (!ip) {
            console.error(`Failed to resolve printer ip for mac ${this.mac} in arp cache. Starting network scan.`);
            // Attempting network scan
            try {
                // timeout the network scan after a certain time
                ip = await this.findPrinterIpWithNetworkScan();
                if (!ip) {
                    console.error(`Failed to resolve printer ip for mac ${this.mac} in network scan.`);
                    return false;
                }
            }
            catch (error) {
                console.error(`Failed to resolve printer ip for mac ${this.mac} in network scan: ${error}`);
                return false;
            }
        }
        console.log(`Resolved printer ip for mac ${this.mac} to ${ip}`);

        this.thermalPrinter = new ThermalPrinter({
            type: PrinterTypes.EPSON,
            interface: `tcp://${ip}`,
        });
        return true;
    }

    /**
     * Checks if the printer is connected.
     * @returns true if the printer is connected, false otherwise.
     */
    async isConnected(): Promise<boolean> {
        const initSuccessul = await this.init();
        if (!initSuccessul) {
            return false;
        }
        return process.env.DRY_RUN_PRINTER_MAC_ADDRESS === this.mac || (await this.thermalPrinter!.isPrinterConnected());
    }

    /**
     * Prints the given print job.
     * @param printJob The print job to print.
     * @throws error if the printer is not connected or if the print job could not be printed.
     */
    async print(printJob: PrintQueueJobDto): Promise<void> {
        // Initialize the printer if it is not already initialized
        const initSuccessul = await this.init();
        if (!initSuccessul) {
            throw new Error(`Failed to initialize printer ${this.name} with mac ${this.mac}`);
        }

        // Check if the printer is connected
        if (!this.isConnected()) {
            throw new Error(`Failed to print job because printer ${this.name} with mac ${this.mac} is not connected`);
        }

        // Dry run the print job if in dry run mode
        if (process.env.DRY_RUN == "true") {
            return this.dryRun(printJob);
        }

        // Print the job
        // Print the logo if available
        if (printJob.base64PngLogoImage) {
            this.thermalPrinter!.alignCenter();
            const imageBuffer = Buffer.from(printJob.base64PngLogoImage, 'base64');
            await this.thermalPrinter!.printImageBuffer(imageBuffer);
            this.thermalPrinter!.alignLeft();
            this.thermalPrinter!.newLine();
            this.thermalPrinter!.newLine();
        }

        // print header if available
        if (printJob.header) {
            this.thermalPrinter!.alignCenter();
            this.thermalPrinter!.println(printJob.header);
            this.thermalPrinter!.alignLeft();
            this.thermalPrinter!.newLine();
        }

        // Print title if available
        if (printJob.title) {
            this.thermalPrinter!.alignCenter();
            this.thermalPrinter!.bold(true);
            this.thermalPrinter!.setTextQuadArea();
            this.thermalPrinter!.println(printJob.title);
            this.thermalPrinter!.setTextNormal();
            this.thermalPrinter!.bold(false);
            this.thermalPrinter!.alignLeft();
            this.thermalPrinter!.newLine();
        }

        // Print body
        this.thermalPrinter!.println(printJob.body);

        // Print QR code if available
        if (printJob.qrCode) {
            this.thermalPrinter!.alignCenter();
            this.thermalPrinter!.newLine();
            this.thermalPrinter!.printQR(printJob.qrCode);
            this.thermalPrinter!.alignLeft();
            this.thermalPrinter!.newLine();
        }

        // Print footer if available
        if (printJob.footer) {
            this.thermalPrinter!.newLine();
            this.thermalPrinter!.println(printJob.footer);
        }

        // Cut the paper and execute the print job
        this.thermalPrinter!.cut();
        try {
            await this.thermalPrinter!.execute({ docname: printJob.id, waitForResponse: false });
        } catch (error) {
            throw new Error(`Failed to print job ${printJob.id} on printer ${this.name} with mac ${this.mac}: ${error}`);
        } finally {
            await this.thermalPrinter!.clear();
        }
    }

    private async findPrinterIpWithNetworkScan(): Promise<string | null> {
        const networks = os.networkInterfaces();
        for (const networkName in networks) {
            // we only scan networks prefixed with "eth" or "wlan"
            if (!networkName.startsWith("eth") && !networkName.startsWith("wlan") && !networkName.startsWith("Ethernet") && !networkName.startsWith("Wi-Fi")) {
                continue;
            }
            
            const network = networks[networkName];

            // skip all internal networks
            if (!network) {
                continue;
            }

            for (const networkInterface of network) {
                if (networkInterface.family === "IPv4") {
                    const cidr = networkInterface.cidr;
                    // If cidr is not available we skip this network
                    if(!cidr) {
                        console.warn(`Skipping network ${networkName} because no cidr is available`);
                        continue;
                    }

                    // if the network is bigger than /24 we skip it since it would take too long to scan
                    try {
                        const networkSize = parseInt(cidr.split("/")[1]);
                        if(networkSize > 24) {
                            console.warn(`Skipping network ${networkName} because it is bigger than /24`);
                            continue;
                        }
                    } catch (error) {
                        console.warn(`Skipping network ${networkName} because we could not parse the cidr: ${cidr}`, error);
                        continue;
                    }

                    console.log(`Scanning network ${networkName} with cidr ${cidr}`);
                    const arrping = new Arpping({
                        timeout: networkScanMaxTimeoutInS,
                        interfaceFilters: {
                            family: ["IPv4"],
                            // @ts-ignore (somehow the type definition is wrong here and does not support windows)
                            interface: [networkName],
                            internal: [false]
                        },
                        useCache: false,
                        debug: true
                    })
            
                    const result = await arrping.searchByMacAddress([this.mac])
                    if (result.hosts.length === 0) {
                        console.log(`No host found in network ${networkName} with mac ${this.mac}`);
                        continue;
                    }

                    if (result.hosts.length > 1) {
                        console.warn(`Found more than one host with mac ${this.mac}. This should not happen??. Better check the network configuration.`);
                    }

                    const printer = result.hosts[0];
                    console.log(`Discovered Printer device`, printer);
                    return printer.ip;
                }
            }

        }

        // On windows it sometimes still does not find the printer and we need to check the arp cache again
        if (process.platform === "win32") {
            console.warn(`Network scan on windows failed. This somehow happens but the arp cache may still be populated. Trying to resolve printer ip for mac ${this.mac} in arp cache again.`);
            return await arp.toIP(this.mac);
        }

        // If we reach this point we did not find the printer
        console.error(`Failed to find printer with mac ${this.mac} in any network`);
        return null;
    }

    /**
     * Dry run the print job. This will not actually print the job but only log the output.
     */
    private dryRun(printJob: PrintQueueJobDto): void {
        const virtualPaperWidth = 48;

        const center = (text: string): string => {
            const resultLines: string[] = [];
            text.split("\n").forEach(line => {
                const whiteSpaces = Math.floor((virtualPaperWidth - line.length) / 2);
                let resultLine = "";
                for (let i = 0; i < whiteSpaces; i++) {
                    resultLine += " ";
                }
                resultLine += line;
                resultLines.push(resultLine);
            });
            return resultLines.join("\n");
        }

        const splitIfTooLong = (text: string): string => {
            if (text.length > virtualPaperWidth) {
                const result = [];
                const words = text.split(" ");
                let line = "";
                for (let i = 0; i < words.length; i++) {
                    if (line.length + words[i].length + 1 > virtualPaperWidth) {
                        result.push(line);
                        line = words[i];
                    } else {
                        if (line.length > 0) {
                            line += " ";
                        }
                        line += words[i];
                    }
                }
                result.push(line);
                return result.join("\n");
            } else {
                return text;
            }
        }

        let virtualPaper = "\n";
        if (printJob.base64PngLogoImage) {
            virtualPaper += center("<LOGO>");
            virtualPaper += "\n\n";
        }
        if (printJob.header) {
            virtualPaper += center(splitIfTooLong(printJob.header));
            virtualPaper += "\n\n";
        }
        if (printJob.title) {
            virtualPaper += center(splitIfTooLong(`*${printJob.title}*`));
            virtualPaper += "\n\n";
        }

        virtualPaper += splitIfTooLong(printJob.body) + "\n\n";

        if (printJob.qrCode) {
            virtualPaper += center("<QR-CODE>");
            virtualPaper += "\n\n";
        }
        if (printJob.footer) {
            virtualPaper += center(splitIfTooLong(printJob.footer));
            virtualPaper += "\n\n";
        }

        // add frame around the virtual paper
        // +-----..-----+
        // |            |
        // ..           ..
        // |            |
        // +-----..-----+
        let frame = "";
        for (let i = 0; i < virtualPaperWidth; i++) {
            frame += "-";
        }
        frame = "+-" + frame + "-+";
        const virtualPaperLines = virtualPaper.split("\n");
        for (let i = 0; i < virtualPaperLines.length; i++) {
            if (virtualPaperLines[i].length < virtualPaperWidth) {
                const whiteSpaces = virtualPaperWidth - virtualPaperLines[i].length;
                for (let j = 0; j < whiteSpaces; j++) {
                    virtualPaperLines[i] += " ";
                }
            }
            virtualPaperLines[i] = "| " + virtualPaperLines[i] + " |";
        }
        virtualPaper = `${frame}\n${virtualPaperLines.join("\n")}\n${frame}\n`;

        console.log(virtualPaper);
    }
}