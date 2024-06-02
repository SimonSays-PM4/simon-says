import { useContext, useEffect, useRef, useState } from "react";
import { AppContext } from "../providers/AppContext";
import { OrderDTO } from "../gen/api";
import { SocketApi } from 'printer-api-lib/src/socket-api';
import { PrintQueueJobDto } from "printer-api-lib/src/dtos";
import { FaExclamation, FaPrint, FaRegClock, FaSync, FaTimesCircle } from "react-icons/fa";

const baseUrl = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;
const printerServerId = process.env.VITE_PRINTER_SERVER_ID || import.meta.env.VITE_PRINTER_SERVER_ID;
const receiptPrinterQueueId = process.env.VITE_RECEIPT_PRINTER_QUEUE_ID || import.meta.env.VITE_RECEIPT_PRINTER_QUEUE_ID;

export const PrintJobStatusIndicator: React.FC<{ order: OrderDTO }> = ({ order }) => {
    const { loginInfo } = useContext(AppContext);
    const elementRef = useRef<HTMLDivElement>(null);
    const [printJobStatus, setPrintJobStatus] = useState<PrintQueueJobDto | undefined>(undefined);
    const socketApiRef = useRef<SocketApi<PrintQueueJobDto, PrintQueueJobDto> | undefined>(undefined);

    let onElementIsVisible = () => {
        socketApiRef.current = SocketApi.connectToPrintQueueJob(
            baseUrl,
            printerServerId,
            receiptPrinterQueueId,
            order.id.toString(),
            (initialData) => setPrintJobStatus(initialData),
            (updatedData) => setPrintJobStatus(updatedData),
            (_) => setPrintJobStatus(undefined),
            (applicationError) => console.error(applicationError),
            `Basic ${btoa(`${loginInfo.userName}:${loginInfo.password}`)}`
        );
    };

    let onElementIsNotVisible = () => {
        socketApiRef.current?.disconnect();
    };

    useEffect(() => {
        // We only connect to the socket if the element is visible. This is to avoid unnecessary connections.
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    onElementIsVisible();
                } else {
                    onElementIsNotVisible();
                }
            });
        });

        if (elementRef.current) {
            observer.observe(elementRef.current);
        }

        return () => {
            if (elementRef.current) {
                observer.unobserve(elementRef.current);
            }

            socketApiRef.current?.disconnect();
        };
    }, []);

    //const printJobStatus = useContext(PrintJobStatesContext).get(order.id.toString()); 

    const statusJsx = getStatusJsxForPrintJob(printJobStatus);

    const restartPrintJob = async () => {
        await socketApiRef.current?.sendChange({ id: printJobStatus?.id?.toString(), status: "PENDING", statusMessage: "Requeued" });
    }

    let restartPrintJobButton = () => {
        // grey out the button if the print job is not in an error state
        if (!printJobStatus || printJobStatus?.status === "PENDING") {
            return (
                <div
                    className="flex items-center mt-1 cursor-not-allowed text-gray-400"
                    title="Der Druckauftrag kann nur neu gestartet werden, wenn einen Fehler auftrat oder bereits gedruckt wurde."
                >
                    <FaSync />
                    <div className="ml-2">Neu ausdrucken</div>
                </div>
            );
        }

        return (
            <div className="flex items-center mt-1 cursor-pointer hover:text-primary" onClick={() => restartPrintJob()}>
                <FaSync />
                <div className="ml-2">Neu ausdrucken</div>
            </div>
        );
    }

    return ( // put the children next to each other horizontally
        <div ref={elementRef}>
            {statusJsx}
            {restartPrintJobButton()}
        </div>
    );
}

function getStatusJsxForPrintJob(printJobStatus: PrintQueueJobDto | undefined) {
    switch (printJobStatus?.status) {
        case "PENDING":
            return (
                <div className="flex items-center">
                    <FaRegClock />
                    <div className="ml-2">Ausstehend</div>
                </div>
            );
        case "PRINTED":
            return (
                <div className="flex items-center">
                    <FaPrint color="green" />
                    <div className="ml-2">Gedruckt</div>
                </div>
            );
        case "ERROR":
            return (
                <div className="flex items-center" title={printJobStatus?.statusMessage}>
                    <FaExclamation color="red" />
                    <div className="ml-2">Fehler</div>
                </div>
            );
        case "CANCELED":
            return (
                <div className="flex items-center" title={printJobStatus?.statusMessage}>
                    <FaTimesCircle />
                    <div className="ml-2">Abgebrochen</div>
                </div>
            );
        default:
            return <p>Unbekannt</p>;
    }
}