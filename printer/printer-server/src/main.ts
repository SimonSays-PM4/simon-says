import arp from "@network-utils/arp-lookup";

import { ThermalPrinter, PrinterTypes} from "node-thermal-printer";

(async () => {


let mac = await arp.toMAC("192.168.1.84");
console.log("MAC: ", mac);
let ip = await arp.toIP("50:57:9c:d2:12:53");
console.log("IP: ", ip);

let printer = new ThermalPrinter({
    type: PrinterTypes.EPSON,
    interface: 'tcp://192.168.1.84',
});

let isConnected = await printer.isPrinterConnected();
console.log("Is printer connected: ", isConnected);
/*
printer.alignCenter();
await printer.printImage("./assets/cereal-guy-meme-face.png");
printer.newLine();
printer.println("De fucking Drucker funktioniert :D");
printer.newLine();
printer.printQR("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
printer.cut();
let result = await printer.execute();
console.log("Print result: ", result);*/

})();