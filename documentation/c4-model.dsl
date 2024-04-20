workspace "Simon Says" "This workspace illustrates the software system designed to enhance the gastronomy sector's production cycle, particularly for festivals in Illnau-Effretikon." {

    model {
        organizer = person "Organizer" "Festival organizer setting up events, menus, and stations in the system."
        kitchenStaff = person "Kitchen Staff" "Staff working at different stations, preparing orders."
        waiter = person "Waiter" "Takes orders from customers and logs in with an event code to place orders."


        softwareSystem = softwareSystem "Simon Says" {
            description "A system to aid the production cycle in the gastronomy sector for festivals."

            kitchenDisplay = container "Kitchen Display" "Display interface for the kitchen staff, shown on tablets. Provides real-time information about orders and tasks." "React, Tablet View" "Tablet"
            adminDashboard = container "Admin Dashboard" "Web-based admin dashboard for organizers to manage events, menus, stations, and view reports." "React, Web Application" "Dashboard"
            waiterView = container "Waiter View" "Mobile app interface for waiters to take and manage orders, accessible via smartphones." "React, Smartphone View" "Smartphone"
            webAppBackend = container "Web Application Backend" "The backend of the web application, built with Kotlin. Handles business logic, data processing, and server-side tasks. Dependencies: Spring Boot Starter Web, Springdoc OpenAPI" "Spring Boot"
            database = container "Database" "Stores event, menu, station, and order data." "MySQL Database" "Database"
            printServer = container "Print Server" "Manages printing of orders and receipts." "Raspberry Pi"
        }


        kitchenStaff -> kitchenDisplay "Views and completes tasks using"
        webAppBackend -> database "Reads from and writes to" "SQL"
        kitchenDisplay -> webAppBackend "Makes API calls to" "REST"
        adminDashboard -> webAppBackend "Makes API calls to" "REST"
        waiterView -> webAppBackend "Makes API calls to" "REST"
        webAppBackend -> printServer "Sends print jobs to" "Socket.io"
        waiter -> waiterView "Places orders and logs in using"
        organizer -> adminDashboard "Configures events, menus, and stations using"



        printer = element "Printer" {
            description "Prints receipts for easy handling in the kitchen."
            tags "External"
        }

        printerSystem = softwareSystem "Printer" {
            description "Prints receipts for guests and for the kitchen"
            tags "External"
            printer1 = container "Printer Container"

        }
        printServer -> printer1 "Sends print jobs to" "IPP"
        softwareSystem -> printer "Sends print jobs to"


    }

    views {
        systemContext softwareSystem "SystemContext" {
            include softwareSystem organizer kitchenStaff waiter printer
            autoLayout
        }

        container softwareSystem "Containers" {
            include *
            autoLayout
        }

        styles {
            element "Database" {
                shape Cylinder
            }
            element "Tablet" {
                shape WebBrowser
            }
            element "Smartphone" {
                shape MobileDeviceLandscape
            }
            element "Dashboard" {
                shape WebBrowser
            }
            element "Printer Container" {

            }
        }
        theme default
    }


}
