```text
   _____ _                      _____                                                       
  / ___/(_)___ ___  ____  ____ / ___/____ ___  _______                                      
  \__ \/ / __ `__ \/ __ \/ __ \\__ \/ __ `/ / / / ___/                                      
 ___/ / / / / / / / /_/ / / / /__/ / /_/ / /_/ (__  )                                       
/____/_/_/ /_/ /_/\____/_/ /_/____/\__,_/\__, /____/                                        
                   ____       _       __/____/            ___    ____  ____     __    _ __  
                  / __ \_____(_)___  / /____  _____      /   |  / __ \/  _/    / /   (_) /_ 
                 / /_/ / ___/ / __ \/ __/ _ \/ ___/_____/ /| | / /_/ // /_____/ /   / / __ \
                / ____/ /  / / / / / /_/  __/ /  /_____/ ___ |/ ____// /_____/ /___/ / /_/ /
               /_/   /_/  /_/_/ /_/\__/\___/_/        /_/  |_/_/   /___/    /_____/_/_.___/ 
                                                                                            
```

This package contains the API library for the printer-queue-server. It is intended to be used in frontends but also for backend services that need to communicate with the printer-queue-server like the on-site printer-server.

The api exposes a REST but also a socket.io interface for communication.

## Getting Started with Printer API

To add the printer-api-lib to your project, run the following command:

```bash
npm install --link <path to printer-api-lib>
```

Since the printer-api-lib is not published to npm, the package is only available locally. The `--link` flag creates a symlink to the printer-api-lib package in your project's `node_modules` folder.

Because the printer-api-lib is written in TypeScript, you need to have a TypeScript compiler installed in your project.
