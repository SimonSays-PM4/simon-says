module.exports = {
    env: {
        browser: true,
        es2021: true,
    },
    extends: [
        "eslint:recommended",
        "plugin:react/recommended",
    ],
    parserOptions: {
        ecmaVersion: "latest",
        ecmaFeatures: {
            jsx: true
        },
        sourceType: "module",
    },
    settings: {
        react: {
            version: "detect"
        },
    },
    plugins: ["react"],
    rules: {
        "react/react-in-jsx-scope": "off",
        "react/jsx-uses-vars": "error",
        "semi": ["error", "always"],
        "no-console": "warn",
    },
};