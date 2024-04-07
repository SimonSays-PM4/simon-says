import {defineConfig, loadEnv} from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {

    const env = loadEnv(mode, process.cwd(), '')
    let api_url = "127.0.0.1:8080/api";
    if (mode != 'development') {
        api_url = env.API_URL;
    }

    return {
        define:{
            __APP_EN__: JSON.stringify(env.APP_ENV),
            __API_URL__: JSON.stringify(api_url)
        },
        plugins: [react()],
            server: {
        host: "localhost",
            port: 3000,
            open: true
    }
    }
});
