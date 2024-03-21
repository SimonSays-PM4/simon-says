import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";

import "./styles/tailwind.css";
import '@radix-ui/themes/styles.css';
import "./styles/index.css";
import { Theme } from "@radix-ui/themes";

const root = document.getElementById("root")!;
if (root) {
    ReactDOM.createRoot(root).render(
        <React.StrictMode>
            <Theme>
                <App />
            </Theme>
        </React.StrictMode>
    );
}
