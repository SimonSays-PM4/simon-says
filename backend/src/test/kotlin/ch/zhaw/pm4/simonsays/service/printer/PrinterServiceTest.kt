package ch.zhaw.pm4.simonsays.service.printer

import ch.zhaw.pm4.simonsays.api.controller.printer.PrintQueueJobsNamespace
import ch.zhaw.pm4.simonsays.api.controller.printer.PrinterServersNamespace
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDto
import ch.zhaw.pm4.simonsays.config.PrinterProperties
import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PrinterServiceTest {
    private val paperWidth = 50

    private val printQueueJobService: PrintQueueJobService = mockk(relaxed = true)
    private val printerProperties: PrinterProperties = mockk(relaxed = true)
    private val printQueueJobsNamespace: PrintQueueJobsNamespace = mockk(relaxed = true)
    private val printerServersNamespace: PrinterServersNamespace = mockk(relaxed = true)

    private lateinit var printerService: PrinterService

    @BeforeEach
    fun setUp() {
        printerService = PrinterService(
            printerProperties,
            printQueueJobService,
            printQueueJobsNamespace,
            printerServersNamespace
        )
        every { printerProperties.receiptMaxCharactersPerLine } returns paperWidth
        // Enable dry for all tests
        every { printerProperties.dryRun } returns true
    }

    @Test
    fun `test takeaway order title contains takeaway number`() {
        val takeawayNumber = 420L
        val takeawayOrder: FoodOrder = mockk(relaxed = true)
        every { takeawayOrder.isTakeAway } returns true
        every { takeawayOrder.getTakeAwayNr() } returns takeawayNumber

        val printJobs = printerService.printFoodOrder(takeawayOrder)

        assertEquals(2, printJobs.size)
        assertTrue(printJobs[0].title!!.contains(takeawayNumber.toString()))
        assertTrue(printJobs[1].title!!.contains(takeawayNumber.toString()))
    }

    @Test
    fun `test takeaway order body contains two menu items`() {
        val burgerMenuItem: OrderMenuItem = mockk(relaxed = true)
        every { burgerMenuItem.name } returns "Burger"
        every { burgerMenuItem.price } returns 10.0

        val friesMenuItem: OrderMenuItem = mockk(relaxed = true)
        every { friesMenuItem.name } returns "Fries"
        every { friesMenuItem.price } returns 5.0

        val takeawayOrder: FoodOrder = mockk(relaxed = true)
        every { takeawayOrder.isTakeAway } returns true
        every { takeawayOrder.getTakeAwayNr() } returns 420L
        every { takeawayOrder.menuItems } returns mutableListOf(burgerMenuItem, friesMenuItem)
        every { takeawayOrder.totalPrice } returns 15.0

        val printJobs = printerService.printFoodOrder(takeawayOrder)

        assertEquals(2, printJobs.size)
        assertTrue(printJobs[0].body.contains("Burger"))
        assertTrue(printJobs[0].body.contains("Fries"))
        assertTrue(printJobs[1].body.contains("Burger"))
        assertTrue(printJobs[1].body.contains("Fries"))
    }

    @Test
    fun `test takeaway order body contains the price of a menu item`() {
        val burgerMenuItem: OrderMenuItem = mockk(relaxed = true)
        every { burgerMenuItem.name } returns "Burger"
        every { burgerMenuItem.price } returns 10.0

        val takeawayOrder: FoodOrder = mockk(relaxed = true)
        every { takeawayOrder.isTakeAway } returns true
        every { takeawayOrder.getTakeAwayNr() } returns 420L
        every { takeawayOrder.menuItems } returns mutableListOf(burgerMenuItem)
        every { takeawayOrder.totalPrice } returns 10.5

        val printJobs = printerService.printFoodOrder(takeawayOrder)

        assertEquals(2, printJobs.size)
        assertTrue(printJobs[0].body.contains("10.00"))
        assertTrue(printJobs[1].body.contains("Burger"))
        assertTrue(printJobs[1].body.contains("10.00"))
    }

    @Test
    fun `test takeaway order body contains ingredients of a menu item on internal receipt and not on external`() {
        val buns: OrderIngredient = mockk(relaxed = true)
        every { buns.name } returns "Buns"
        val meat: OrderIngredient = mockk(relaxed = true)
        every { meat.name } returns "Meat"

        val burgerMenuItem: OrderMenuItem = mockk(relaxed = true)
        every { burgerMenuItem.name } returns "Burger"
        every { burgerMenuItem.price } returns 10.0
        every { burgerMenuItem.orderIngredients } returns mutableListOf(buns, meat)

        val takeawayOrder: FoodOrder = mockk(relaxed = true)
        every { takeawayOrder.isTakeAway } returns true
        every { takeawayOrder.getTakeAwayNr() } returns 420L
        every { takeawayOrder.menuItems } returns mutableListOf(burgerMenuItem)
        every { takeawayOrder.totalPrice } returns 10.5

        val printJobs = printerService.printFoodOrder(takeawayOrder)

        assertEquals(2, printJobs.size)
        // External receipt should not contain ingredients
        assertTrue(printJobs[0].body.contains("Buns"))
        assertTrue(printJobs[0].body.contains("Meat"))
        // Internal receipt should contain ingredients
        assertFalse(printJobs[1].body.contains("Buns"))
        assertFalse(printJobs[1].body.contains("Meat"))
    }

    @Test
    fun `long menu item name and long ingredients are forced on new lines`() {
        val longMenuItemName = "This is a very long menu item name that should be split onto multiple lines"
        val longIngredientName = "This is a very long ingredient name that should be split onto multiple lines"
        val longIngredient: OrderIngredient = mockk(relaxed = true)
        every { longIngredient.name } returns longIngredientName
        val longMenuItem: OrderMenuItem = mockk(relaxed = true)
        every { longMenuItem.name } returns longMenuItemName
        every { longMenuItem.price } returns 10000.0
        every { longMenuItem.orderIngredients } returns mutableListOf(longIngredient)

        val takeawayOrder: FoodOrder = mockk(relaxed = true)
        every { takeawayOrder.isTakeAway } returns true
        every { takeawayOrder.getTakeAwayNr() } returns 420L
        every { takeawayOrder.menuItems } returns mutableListOf(longMenuItem)
        every { takeawayOrder.totalPrice } returns 10000.0

        val printJobs = printerService.printFoodOrder(takeawayOrder)

        assertEquals(2, printJobs.size)
        assertTrue(printJobs[0].body.contains("This is a very long menu item name that"))
        assertTrue(printJobs[0].body.contains("should be split onto multiple lines"))
        assertTrue(printJobs[0].body.contains("(This is a very long ingredient name th-"))
        assertTrue(printJobs[0].body.contains("at should be split onto multiple lines)"))
        assertTrue(printJobs[1].body.contains("This is a very long menu item name that"))
        assertTrue(printJobs[1].body.contains("should be split onto multiple lines"))
    }

    @Test
    fun `Receipt includes header, footer and logo for non-takeaway order`() {
        val header = "Sample Header"
        val footer = "Sample Footer"
        val logo = "Sample Logo"
        val qrCode = "Sample QR Code"
        val nonTakeawayOrder: FoodOrder = mockk(relaxed = true)
        every { nonTakeawayOrder.isTakeAway } returns false
        every { printerProperties.receiptHeader } returns header
        every { printerProperties.receiptFooter } returns footer
        every { printerProperties.receiptBase64PngLogo } returns logo
        every { printerProperties.receiptQrCodeContent } returns qrCode

        val printJobs = printerService.printFoodOrder(nonTakeawayOrder)

        assertTrue(
            printJobs.any { it.header == header }, "Header should be included in the print job for non-takeaway orders"
        )
        assertTrue(
            printJobs.any { it.footer == footer }, "Footer should be included in the print job for non-takeaway orders"
        )
        assertTrue(
            printJobs.any { it.base64PngLogoImage == logo },
            "Logo should be included in the print job for non-takeaway orders"
        )
        assertTrue(
            printJobs.any { it.qrCode == qrCode }, "QR code should be included in the print job for non-takeaway orders"
        )
    }

    @Test
    fun `Receipt does not include header, footer and logo for internal takeaway order`() {
        val header = "Sample Header"
        val footer = "Sample Footer"
        val logo = "Sample Logo"
        val qrCode = "Sample QR Code"
        val takeawayOrder: FoodOrder = mockk(relaxed = true)
        every { takeawayOrder.isTakeAway } returns true
        every { takeawayOrder.getTakeAwayNr() } returns 420L
        every { printerProperties.receiptHeader } returns header
        every { printerProperties.receiptFooter } returns footer
        every { printerProperties.receiptBase64PngLogo } returns logo
        every { printerProperties.receiptQrCodeContent } returns qrCode

        val printJobs = printerService.printFoodOrder(takeawayOrder)

        val internalTakeawayReceipt = printJobs.first()

        assertNull(
            internalTakeawayReceipt.header,
            "Header should not be included in the print job for internal takeaway orders"
        )
        assertNull(
            internalTakeawayReceipt.footer,
            "Footer should not be included in the print job for internal takeaway orders"
        )
        assertNull(
            internalTakeawayReceipt.base64PngLogoImage,
            "Logo should not be included in the print job for internal takeaway orders"
        )
        assertNull(
            internalTakeawayReceipt.qrCode,
            "QR code should not be included in the print job for internal takeaway orders"
        )
    }

    @Test
    fun `print stores job in database and informs namespace`() {
        val order: FoodOrder = mockk(relaxed = true)
        val printQueueJobDto: PrintQueueJobDto = mockk(relaxed = true)
        every { printerProperties.dryRun } returns false
        every { order.isTakeAway } returns false
        every { printQueueJobService.savePrintQueueJob(any(), any()) } returns printQueueJobDto
        every { printQueueJobsNamespace.onChange(any()) } just Runs

        printerService.printFoodOrder(order)

        verify { printQueueJobService.savePrintQueueJob(any(), any()) }
        verify { printQueueJobsNamespace.onChange(eq(printQueueJobDto)) }
    }

    @Test
    fun `print handles error and informs namespaces`() {
        val order: FoodOrder = mockk(relaxed = true)
        val error = "Error"
        every { printerProperties.dryRun } returns false
        every { order.isTakeAway } returns false
        every { printQueueJobService.savePrintQueueJob(any(), any()) } throws Exception(error)
        every { printQueueJobsNamespace.onApplicationError(any<String>(), any(), any()) } just Runs
        every { printerServersNamespace.onApplicationError(any<String>(), any(), any()) } just Runs

        printerService.printFoodOrder(order)

        verify { printQueueJobsNamespace.onApplicationError(any<String>(), any(), any()) }
        verify { printerServersNamespace.onApplicationError(any<String>(), any(), any()) }
    }
}