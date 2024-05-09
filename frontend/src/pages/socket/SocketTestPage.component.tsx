import { useSocketTestPage } from "./SocketTestPage.hooks";

export const SocketTestPage: React.FC = () => {
    const { isConnected } = useSocketTestPage();

    return (
        <div>
            <h1>Socket Test Page</h1>

            {isConnected ? <p>Connected</p> : <p>Not connected</p>}
        </div>
    );
}