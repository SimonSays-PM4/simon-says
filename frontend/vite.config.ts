import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import tsNameof from "vite-plugin-ts-nameof";
import istanbul from "vite-plugin-istanbul";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), "");
    let api_url = "http://127.0.0.1:8080";
    if (mode != "development") {
        api_url = JSON.stringify(env.VITE_API_URL);
        console.log(api_url);
    }

    return {
        define: {
            process: {
                env: {
                    VITE_API_URL: api_url
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
                requireEnv: true
            })
        ],
        server: {
            host: "localhost",
            port: 3000,
            open: true
        }
    };
});
