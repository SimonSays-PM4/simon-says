import { useEffect, useState } from "react";
import { io } from "socket.io-client";

type SocketTestPageReturnProps = {
    isConnected: boolean;
};

export const useSocketTestPage = (): SocketTestPageReturnProps => {
    const url = process.env.VITE_API_URL || import.meta.env.VITE_API_URL;
    console.log("url", url);
    const socket = io(url + "/socket-api/v1/printer-servers/a35e6a08-35ef-42c1-9a09-39d32fc2a5d6");

    const [isConnected, setIsConnected] = useState<boolean>(socket.connected);

    useEffect(() => {
        function onConnect() {
            setIsConnected(true);
        }

        function onDisconnect() {
            setIsConnected(false);
        }

        socket.on("connect", onConnect);
        socket.on("disconnect", onDisconnect);

        return () => {
            socket.off("connect", onConnect);
            socket.off("disconnect", onDisconnect);
        };
    }, []);


    return { isConnected };
}