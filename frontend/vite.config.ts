import {defineConfig, loadEnv} from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {

    const env = loadEnv(mode, process.cwd(), '');
    let api_url = "http://localhost:8080/";
    if (mode != 'development') {
        api_url = JSON.stringify(env.VITE_API_URL);
        console.log(api_url)
    }

    return {
        define:{
            process: {
                env: {
                    VITE_API_URL: api_url
                }
            },
        },
        plugins: [react()],
            server: {
        host: "localhost",
            port: 3000,
            open: true
    }
    }
});
