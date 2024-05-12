import CryptoJS from "crypto-js";
const secretKey = "my-secret-aes-key"; // This key should be kept secure and not exposed

export const encryptData = (pw: string) => {
    return CryptoJS.AES.encrypt(pw, secretKey).toString();
};

export const decryptData = (encryptedData: string | CryptoJS.lib.CipherParams) => {
    const bytes = CryptoJS.AES.decrypt(encryptedData, secretKey);
    return bytes.toString(CryptoJS.enc.Utf8);
};
