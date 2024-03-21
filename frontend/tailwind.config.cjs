module.exports = {
    content: ["./index.html", "./src/**/*.{ts,tsx}"],
    theme: {
        colors: {
            white: "#FFFFFF",
            black: "#111111",

            primarybackground: "#FFFFFF", //white
            secondarybackground: "#F5F5F5", //lighter gray

            primary: "#50a62a", //red
            hoverprimary: "#358a10", //darker red
            primaryfont: "#FFFFFF", //white

            secondary: "#EAEAEA", //gray
            hoversecondary: "#E3E3E3", //darker gray
            secondaryfont: "#3A3F42", //black

            disabled: "#F7F8F8", //lightgray
            disabledfont: "#999C9D", //lightgray

            primaryborder: "rgba(0,0,0,0.1)", //gray
            inputborder: "#B4B4B4", //gray
            spacingborder: "#E6E6E6", //gray light
        },
        extend: {
            boxShadow: {
                md: "0px 5px 15px rgba(124, 7, 49, 0.35)",
                lg: "0px 2px 8px rgba(0, 0, 0, 0.35), 0px 8px 18px rgba(0, 0, 0, 0.15)",
                xl: "0px 12px 30px rgba(0, 0, 0, 0.1)",
                popup: "0px 12px 32px rgb(0 0 0 / 8%), 0px 44px 80px rgb(0 0 0 / 6%)",
            },
            gridTemplateRows: {
                //specific grid configuration for text fields
                textField: "1fr 1fr 1fr 1fr 5px",
                textArea: "40px 1fr 5px",
            },
        },
    },
};
