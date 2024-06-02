package ch.zhaw.pm4.simonsays.api.types.printer

import io.swagger.v3.oas.annotations.media.Schema

/**
 * The print job is printed in the following format:
 *
 *      ---------------------------
 *      |                         |
 *      |      <logo/image>       |
 *      |                         |
 *      |        <header>         |
 *      |                         |
 *      |        <*title*>        |
 *      |                         |
 *      | <body>                  |
 *      |                         |
 *      |        <QR-code>        |
 *      |                         |
 *      |        <footer>         |
 *      |                         |
 *      ---------------------------
 */
@Schema(description = "A job within a print queue.")
data class PrintQueueJobDTO(
    @Schema(description = "The unique identifier of the print job.")
    val id: String,

    @Schema(description = "The current status of the print job.", enumAsRef = true)
    val status: JobStatusDTO,

    @Schema(description = "An optional message describing the current status of the print job.", required = false)
    val statusMessage: String? = null,

    @Schema(description = "Base64 encoded PNG image for the top of the receipt, optional.", required = false)
    val base64PngLogoImage: String? = null,

    @Schema(description = "Header text for the receipt, optional.", required = false)
    val header: String? = null,

    @Schema(description = "Title text for the receipt, bold and centered, optional.", required = false)
    val title: String? = null,

    @Schema(description = "The main body text of the receipt.")
    val body: String,

    @Schema(description = "QR code text, converted to a QR code on the receipt, optional.", required = false)
    val qrCode: String? = null,

    @Schema(description = "Footer text for the receipt, optional.", required = false)
    val footer: String? = null,

    @Schema(description = "The date and time when the print job was created (in milliseconds since epoch).")
    val creationDateTime: Long,

    @Schema(description = "The date and time when the print job was last updated (in milliseconds since epoch).")
    val lastUpdateDateTime: Long
)
