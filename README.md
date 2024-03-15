```txt
   _____ _                      _____                                                      
  / ___/(_)___ ___  ____  ____ / ___/____ ___  _______                                     
  \__ \/ / __ `__ \/ __ \/ __ \\__ \/ __ `/ / / / ___/                                     
 ___/ / / / / / / / /_/ / / / /__/ / /_/ / /_/ (__  )                                      
/____/_/_/ /_/ /_/\____/_/ /_/____/\__,_/\__, /____/                                       
                   ____       _       __/____/           _____            __               
                  / __ \_____(_)___  / /____  _____     / ___/__  _______/ /____  ____ ___ 
                 / /_/ / ___/ / __ \/ __/ _ \/ ___/_____\__ \/ / / / ___/ __/ _ \/ __ `__ \
                / ____/ /  / / / / / /_/  __/ /  /_____/__/ / /_/ (__  ) /_/  __/ / / / / /
               /_/   /_/  /_/_/ /_/\__/\___/_/        /____/\__, /____/\__/\___/_/ /_/ /_/ 
                                                           /____/                          
```

This directory contains source code important to the printer-system of SimonSays. The printer-system consists of four main parts:

- [printer-server](./printer-server): The on-site printer server. It acts as a bridge between the SimonSays printer-queue-server and the actual printer.
- [printer-queue-server](../backend): The server that manages the printer queue. It is responsible for managing the print jobs and sending them to the on-site printer server. In our case this is the monolithic backend server (see [backend](../backend) directory)
- [printer-api](./printer-api): The API for the printer-queue-server. It is intended to be used for frontends but also for printer-servers that need to communicate with the printer-queue-server
- [printer-user-interface](./frontend): The user interface for the printer-queue-server. It is intended to be used by the staff to manage the printer queue.

## Architecture

```txt
                        -----------------
                        |               |
                        | printer-queue |
                        |    -server    |
                        |               |
                        -----------------
                                |
                                |
                        -----------------
                        |               |
                        |  printer-api  |
                        |               |
                        -----------------    
                            /       \
                          /           \    
                        /               \
                      /                   \
            ----------------        ------------------
            |              |        |                |
            | printer-user |        | printer-server |  
            |  -interface  |        |                |
            |              |        ------------------
            ---------------- 
```
