package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.controller.printer.PrintQueueJobsNamespace
import ch.zhaw.pm4.simonsays.api.controller.printer.PrinterServersNamespace
import ch.zhaw.pm4.simonsays.api.types.printer.JobStatusDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDto
import ch.zhaw.pm4.simonsays.config.PrinterProperties
import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * This service is fundamentally responsible for printing receipt. It acts as highest layer of abstraction for printing
 * and contains the business logic for printing.
 */
@Service
class PrinterService(
    private val printerProperties: PrinterProperties,
    private val printQueueJobService: PrintQueueJobService,
    private val printerQueueJobsNamespace: PrintQueueJobsNamespace,
    private val printerServersNamespace: PrinterServersNamespace
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val maxCharactersPerLineWithoutPrice: Int
        get() {
            val maxCharactersPerLine = printerProperties.receiptMaxCharactersPerLine
            return maxCharactersPerLine - PRICE_COLUMN_WIDTH - 1 // -1 for the "-" symbol
        }

    /**
     * Print a given food order.
     *
     * For normal orders, one receipt is printed that is handed to the customer together with the food. The normal
     * receipt contains the Table number and the ordered items.
     *
     * For takeaway orders, two receipts are printed. One receipt is handed to the customer together with the food
     * and contains the takeaway number and the ordered items. The second receipt is kept by the restaurant and
     * contains the takeaway number and the ordered items. It is used to call the customer when the food is ready.
     *
     * @return a list with one entry for table receipts and two entries for take away
     */
    fun printFoodOrder(foodOrder: FoodOrder): List<PrintQueueJobDto> {
        return if (foodOrder.isTakeAway) {
            printTakeAwayReceipt(foodOrder)
        } else {
            listOf(printNormalReceipt(foodOrder))
        }
    }

    private fun printTakeAwayReceipt(foodOrder: FoodOrder): List<PrintQueueJobDto> {
        val title = "Takeaway #${foodOrder.getTakeAwayNr().toString()}"
        return listOf(
            printInternalTakeAwayReceipt(foodOrder, title), printNormalReceiptWithCustomTitle(foodOrder, title)
        )
    }

    private fun printNormalReceipt(foodOrder: FoodOrder): PrintQueueJobDto {
        val title = "Tisch #${foodOrder.tableNumber}"
        return printNormalReceiptWithCustomTitle(foodOrder, title)
    }

    private fun printNormalReceiptWithCustomTitle(foodOrder: FoodOrder, title: String): PrintQueueJobDto {
        return print(
            orderId = foodOrder.id!!,
            isTakeaway = foodOrder.isTakeAway,
            title = title,
            body = getBodyForFoodOrder(foodOrder),
            base64PngLogoImage = printerProperties.receiptBase64PngLogo,
            header = printerProperties.receiptHeader,
            qrCode = printerProperties.receiptQrCodeContent,
            footer = printerProperties.receiptFooter,
        )
    }

    /**
     * Print a receipt that is to be used internally in the kitchen. No fancy QR codes or anything
     */
    fun printInternalTakeAwayReceipt(foodOrder: FoodOrder, title: String): PrintQueueJobDto {
        return print(
            orderId = foodOrder.id!!,
            isTakeaway = foodOrder.isTakeAway,
            title = title,
            body = getBodyForFoodOrder(foodOrder)
        )
    }

    private fun print(
        orderId: Long,
        isTakeaway: Boolean,
        title: String,
        body: String,
        qrCode: String? = null,
        footer: String? = null,
        header: String? = null,
        base64PngLogoImage: String? = null
    ): PrintQueueJobDto {
        val currentMs = Instant.now().toEpochMilli()
        val printQueueJobDto = PrintQueueJobDto(
            id = orderId.toString(),
            title = title,
            body = body,
            base64PngLogoImage = base64PngLogoImage,
            header = header,
            qrCode = qrCode,
            footer = footer,
            creationDateTime = currentMs,
            lastUpdateDateTime = currentMs,
            status = JobStatusDto.PENDING
        )

        val printerQueueId = if (isTakeaway) {
            printerProperties.takeawayPrinterQueueId
        } else {
            printerProperties.receiptPrinterQueueId
        }

        if (printerProperties.dryRun) {
            dryRun(printerQueueId, printQueueJobDto)
        } else {
            try {
                // Save to our database
                val savedPrintQueueJob = printQueueJobService.savePrintQueueJob(printerQueueId, printQueueJobDto)
                // send change to sockets
                printerQueueJobsNamespace.onChange(savedPrintQueueJob)
            } catch (e: Exception) {
                val errorMessage = "Failed to create print job for order $orderId due to " + e.message
                val errorCode = "CREATE_PRINT_JOB_FAILED"
                log.error(errorMessage, e)
                printerQueueJobsNamespace.onApplicationError(
                    printQueueJobDto.id, errorCode, errorMessage
                )
                printerServersNamespace.onApplicationError(
                    printerQueueId, errorCode, errorMessage
                )
            }
        }
        return printQueueJobDto
    }


    /*
     * Output example:
    +------------------------------+
    | Burger                 10.20 |
    | (lettuce, tomato, pa-        |
    | ddy, bread)                  |
    | ---------------------------- |
    | Total                  10.20 |
    +------------------------------+
     */
    private fun getBodyForFoodOrder(foodOrder: FoodOrder): String {
        var body = ""

        // Add list of ordered items
        body += foodOrder.menuItems?.map { getTextForOrderMenuItem(it) }?.joinToString("\n")
        body += "\n"

        // Add total
        body += createLineSeparator()
        body += "\n"
        body += "Total".padEnd(maxCharactersPerLineWithoutPrice) + formatPriceWithStartPadding(foodOrder.totalPrice)
        return body
    }

    private fun getTextForOrderMenuItem(orderMenuItem: OrderMenuItem): String {
        // print price always with two decimal places
        val price = formatPriceWithStartPadding(orderMenuItem.price)
        val menuItemNameLines = splitPricedItemTextOntoMultipleLinesIfNecessary(orderMenuItem.name)

        var menuItemText = menuItemNameLines.first().padEnd(maxCharactersPerLineWithoutPrice) + price
        // add the other lines of the title
        if (menuItemNameLines.size > 1) {
            menuItemText += "\n"
            menuItemText += menuItemNameLines.subList(1, menuItemNameLines.size).joinToString("\n")
        }

        // if we have no ingredients, we are done
        if (orderMenuItem.orderIngredients.isEmpty()) {
            return menuItemText
        }

        // add the ingredients
        menuItemText += "\n"

        var ingredientsText = orderMenuItem.orderIngredients.joinToString(", ") { it.name }
        // put brackets around it
        ingredientsText = "($ingredientsText)"
        ingredientsText = splitPricedItemTextOntoMultipleLinesIfNecessary(ingredientsText).joinToString("\n")
        menuItemText += ingredientsText

        return menuItemText
    }

    private fun splitPricedItemTextOntoMultipleLinesIfNecessary(text: String): List<String> {
        val lines = mutableListOf<String>()
        var currentLine = ""
        text.forEach { char ->
            if (currentLine.length >= maxCharactersPerLineWithoutPrice) {
                // Check if the last or next character is a space before adding "-"
                if (currentLine.isNotEmpty() && (currentLine.last().isWhitespace() || char.isWhitespace())) {
                    lines.add(currentLine)
                } else {
                    lines.add("$currentLine-")
                }
                currentLine = ""
            }

            // if the first character on a line would be blank we emit it
            if (currentLine.isBlank() && char.isWhitespace()) {
                return@forEach
            }
            currentLine += char
        }
        // Add the remaining part of the text, if any
        if (currentLine.isNotBlank()) {
            lines.add(currentLine)
        }
        return lines
    }

    private fun createLineSeparator(length: Int = printerProperties.receiptMaxCharactersPerLine): String {
        return "-".repeat(length)
    }

    private fun formatPriceWithStartPadding(price: Double): String {
        return String.format("%.2f", price).padStart(PRICE_COLUMN_WIDTH)
    }

    /**
     * Dry run the print job. This will not actually print the job but only log the output.
     */
    private fun dryRun(printerQueueId: String, printJob: PrintQueueJobDto) {
        val virtualPaperWidth = printerProperties.receiptMaxCharactersPerLine

        fun center(text: String): String {
            val resultLines = mutableListOf<String>()
            text.split("\n").forEach { line ->
                val whiteSpaces = (virtualPaperWidth - line.length) / 2
                val resultLine = " ".repeat(whiteSpaces) + line
                resultLines.add(resultLine)
            }
            return resultLines.joinToString("\n")
        }

        var virtualPaper = "\n"
        printJob.base64PngLogoImage?.let {
            virtualPaper += center("<LOGO>")
            virtualPaper += "\n\n"
        }
        printJob.header?.let {
            virtualPaper += center(it)
            virtualPaper += "\n\n"
        }
        printJob.title?.let {
            virtualPaper += center("*${it}*")
            virtualPaper += "\n\n"
        }

        virtualPaper += printJob.body + "\n\n"

        printJob.qrCode?.let {
            virtualPaper += center("<QR-CODE>")
            virtualPaper += "\n\n"
        }
        printJob.footer?.let {
            virtualPaper += center(it)
            virtualPaper += "\n\n"
        }

        // Add frame around the virtual paper
        var frame = "+-" + "-".repeat(virtualPaperWidth) + "-+"
        val virtualPaperLines = virtualPaper.split("\n").map {
            "| ${it.padEnd(virtualPaperWidth)} |"
        }
        virtualPaper = "${frame}\n${virtualPaperLines.joinToString("\n")}\n${frame}\n"

        log.debug(
            """
            Dry run print job:
            Print Job Id: ${printJob.id}
            Print Queue Id: $printerQueueId
            $virtualPaper            
        """.trimIndent()
        )
    }

    companion object {
        private const val PRICE_COLUMN_WIDTH = 10
    }
}