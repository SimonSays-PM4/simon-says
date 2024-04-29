package ch.zhaw.pm4.simonsays.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "printer-queue-server")
class PrinterProperties {
    lateinit var frontendOrigins: Array<String>

    var dryRun = false

    lateinit var printerServerId: String

    lateinit var takeawayPrinterQueueId: String

    lateinit var takeawayPrinterMac: String

    lateinit var receiptPrinterQueueId: String

    lateinit var receiptPrinterMac: String

    var createInitialData: Boolean = true

    var receiptBase64PngLogo: String? = null

    var receiptQrCodeContent: String? = null

    var receiptHeader: String? = null

    var receiptFooter: String? = null

    var receiptMaxCharactersPerLine: Int = 48 // default value if not set
}