import { PrinterServer } from './printer-server';
import 'dotenv/config'
import packageJson from '../package.json'

console.log(`
_____ _                      _____                                                   
/ ___/(_)___ ___  ____  ____ / ___/____ ___  _______                                  
\\__ \\/ / __  __ \\/ __ \\/ __ \\\\__ \\/ __  / / / / ___/                                  
___/ / / / / / / / /_/ / / / /__/ / /_/ / /_/ (__  )                                   
/____/_/_/ /_/ /_/\\____/_/ /_/____/\\__,_/\\__, /____/                                    
                 ____       _       __/____/           _____                          
                / __ \\_____(_)___  / /____  _____     / ___/___  ______   _____  _____
               / /_/ / ___/ / __ \\/ __/ _ \\/ ___/_____\\__ \\/ _ \\/ ___/ | / / _ \\/ ___/
              / ____/ /  / / / / / /_/  __/ /  /_____/__/ /  __/ /   | |/ /  __/ /    
             /_/   /_/  /_/_/ /_/\\__/\\___/_/        /____/\\___/_/    |___/\\___/_/             

Version: ${packageJson.version}
`);

console.log('Starting printer server...');
const printServer = new PrinterServer();
console.log('[ok] Printer server started');

// We also want to attempt to gracefully shut down the server when the process is terminated
function stopServerSignalHandler() {
    printServer.disconnect()
    process.exit()
}

process.on('SIGINT', stopServerSignalHandler)
process.on('SIGTERM', stopServerSignalHandler)
process.on('SIGQUIT', stopServerSignalHandler)