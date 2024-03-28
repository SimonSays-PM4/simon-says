import type { Config } from "tailwindcss";
import colors from "tailwindcss/colors";
import { createThemes } from "tw-colors";

const config: Config = {
    content: ["./src/**/*.{js,ts,jsx,tsx,mdx}", "./index.html"],
    safelist: [
        {
            pattern: /bg-(danger|success|secondary|warning|info)/,
            variants: ["hover"]
        },
        {
            pattern: /border-(danger|success|secondary|warning|info)/
        },
        {
            pattern: /bg-(primary|secondary|success|danger|warning|info)\/75/
        }
    ],
    theme: {
        container: {
            center: true,
            padding: "1rem"
        },

        extend: {
            colors: {
                primary: {
                    ...colors.orange,
                    DEFAULT: "#F58220"
                }
            },

            keyframes: {
                load: {
                    "0%": { width: "0%" },
                    "100%": { width: "100%" }
                }
            },

            spacing: {
                15: "60px",
                18: "72px"
            },

            zIndex: {
                "60": "60",
                "70": "70"
            }
        }
    },

    plugins: [
        createThemes(
            {
                light: {
                    default: colors.slate,
                    primary: {
                        ...colors.orange,
                        DEFAULT: "#F58220"
                    },
                    secondary: "#6c757d",
                    success: "#43d39e",
                    info: "#25c2e3",
                    warning: "#ffbe0b",
                    danger: "#ff5c75",
                    light: "#f8f9fa",
                    dark: "#343a40"
                }
            },
            {
                defaultTheme: "light"
            }
        )
    ]
};
export default config;
