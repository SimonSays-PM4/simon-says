import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import tsNameof from "vite-plugin-ts-nameof";
import istanbul from "vite-plugin-istanbul";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), "");
    let api_url = "http://127.0.0.1:8080";
    let printer_server_id = "a35e6a08-35ef-42c1-9a09-39d32fc2a5d6";
    let take_away_printer_queue_id = "ef084926-d2be-4a03-9538-c8dd44f19e04";
    let receipt_printer_queue_id = "6f7c2fa6-9401-457e-8a86-618e810b103a";
    if (mode != "development") {
        api_url = JSON.stringify(env.VITE_API_URL);
        printer_server_id = env.VITE_PRINTER_SERVER_ID ?? printer_server_id;
        take_away_printer_queue_id = env.VITE_TAKE_AWAY_PRINTER_QUEUE_ID ?? take_away_printer_queue_id;
        receipt_printer_queue_id = env.VITE_RECEIPT_PRINTER_QUEUE_ID ?? receipt_printer_queue_id;
        console.log(api_url);
    }

    return {
        define: {
            APP_VERSION: JSON.stringify(process.env.npm_package_version),
            process: {
                env: {
                    VITE_API_URL: api_url,
                    VITE_PRINTER_SERVER_ID: printer_server_id,
                    VITE_TAKE_AWAY_PRINTER_QUEUE_ID: take_away_printer_queue_id,
                    VITE_RECEIPT_PRINTER_QUEUE_ID: receipt_printer_queue_id
                }
            }
        },
        build: {
            sourcemap: "hidden"
        },
        plugins: [
            react(),
            tsNameof(),
            istanbul({
                extension: [".ts", ".tsx"],
                include: "src/*",
                exclude: ["node_modules", "cypress/*", "src/gen/*"],
                cypress: true,
                requireEnv: false
            })
        ],
        server: {
            host: "localhost",
            port: 3000,
            open: true
        }
    };
});
