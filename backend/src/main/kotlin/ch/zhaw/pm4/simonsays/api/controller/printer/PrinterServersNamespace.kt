package ch.zhaw.pm4.simonsays.api.controller.printer

import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.APPLICATION_ERROR_EVENT
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.CHANGE_EVENT
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.INITIAL_DATA_EVENT
import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace.Companion.REMOVE_EVENT
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.api.types.printer.PrinterServerDto
import ch.zhaw.pm4.simonsays.service.printer.PrinterServerService
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import com.fasterxml.jackson.databind.ObjectMapper
import io.socket.socketio.server.SocketIoSocket
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * This class is responsible for handling the '/socket-api/v1/printer-servers' and
 * '/socket-api/v1/printer-servers/{id}' namespaces.
 */
@Component
class PrinterServersNamespace(
    private val printerServerService: PrinterServerService,
    private val objectMapper: ObjectMapper,
) : SocketIoNamespace<String, PrinterServerDto> {
    companion object {
        /**
         * Regex pattern to match the namespace. The first group is the printer server id.
         */
        internal val namespacePattern = "^/socket-api/v1/printer-servers(?:/([\\w,-]+))?\$".toRegex()
    }

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * A set of all subscribers that are interested in all printer servers.
     */
    internal val subscribersToAllPrinterServers = mutableSetOf<SocketIoSocket>()

    /**
     * A map of all subscribers that are interested in specific printer servers. The key is the printer server id.
     */
    internal val subscribersToSpecificPrinterServer = mutableMapOf<String, MutableSet<SocketIoSocket>>()

    override fun isPartOfNamespace(requestedNamespace: String): Boolean {
        // check if matches the namespace pattern
        val matchResult = namespacePattern.matches(requestedNamespace)
        if (!matchResult) return false

        // check if the printer server id was specified
        val printerServerId = getPrinterServerIdFromNamespace(requestedNamespace)
        // if printer server id is not specified we don't need to further check if the printer server exists
            ?: return true
        // check if the printer server exists
        return printerServerService.doesPrinterServerExist(printerServerId)
    }

    override fun onConnection(socket: SocketIoSocket) {
        val namespace = socket.namespace.name
        val printerServerId = getPrinterServerIdFromNamespace(namespace)

        if (printerServerId == null) {
            subscribersToAllPrinterServers.add(socket)
            val initialData = printerServerService.getAllPrinterServers()
            socket.sendPojo(INITIAL_DATA_EVENT, initialData)
        } else {
            val printerServerSubscribers =
                subscribersToSpecificPrinterServer.computeIfAbsent(printerServerId) { mutableSetOf(socket) }
            printerServerSubscribers.add(socket)
            val initialData = printerServerService.getPrinterServerById(printerServerId)
            socket.sendPojo(INITIAL_DATA_EVENT, initialData)
        }
        socket.on(CHANGE_EVENT) { data -> onChangeEventReceived(data, printerServerId, socket) }
        socket.on(REMOVE_EVENT) { data -> onRemoveEventReceived(data, printerServerId, socket) }
        socket.on(APPLICATION_ERROR_EVENT) { error -> log.warn("Received application error event: $error from socket ${socket.id} with namespace $namespace") }
    }

    /**
     * Invoked when a change event is received on the server
     *
     * @param data The data received with the change event
     * @param subscribedPrinterId The printer server id the client is connected to. {@code null} if the client is connected to all printer servers.
     * @param socket The socket that received the change event
     */
    fun onChangeEventReceived(data: Array<Any>, subscribedPrinterId: String?, socket: SocketIoSocket) {
        log.info("Printer server change event received: $data")

        // Verify that we actually received a json
        if (data.isEmpty() || data.first() !is JSONObject) {
            return onApplicationError(
                socket, code = "INVALID_CHANGE_DATA", message = "Invalid data received. Expected JSON object."
            )
        }
        val printerServerJson = data.first() as JSONObject

        // Get the printer server id if it was provided
        val printerServerId = if (printerServerJson.has("id")) printerServerJson.getString("id") else null

        // Check if the client is connected to a specific printer server and only allow changes to that printer server
        if (subscribedPrinterId != null && printerServerId != subscribedPrinterId) {
            return onApplicationError(
                socket,
                "PROVIDED_AND_SUBSCRIBED_ID_DO_NOT_MATCH",
                "Provided change printer server id does not match the subscribed printer server id."
            )
        }

        // verify that if the id is set, it actually exists
        if (printerServerId != null && !printerServerService.doesPrinterServerExist(printerServerId)) {
            return onApplicationError(
                socket, "PRINTER_SERVER_NOT_FOUND", "Printer server with id $printerServerId not found."
            )
        }

        // if the id is not set, we generate a new one, so we create a new entry
        if (printerServerId == null) {
            printerServerJson.put("id", UUID.randomUUID())
        }

        // Attempt to convert json to data class
        val printerServerDto = try {
            objectMapper.convertValue(printerServerJson, PrinterServerDto::class.java)
        } catch (error: Error) {
            return onApplicationError(
                socket, "INVALID_CHANGE_DATA", "Invalid or malformed data received. Expected PrinterServerDto."
            )
        }

        try {
            val updatePrinterServer = printerServerService.savePrinterServer(printerServerDto)
            // Send updated data to subscribers
            onChange(updatePrinterServer)
        } catch (error: Exception) {
            return onApplicationError(
                socket, "SAVE_ERROR", "Error saving printer server: ${error.message}"
            )
        }
    }

    /**
     * Invoked when a remove event is received on the server
     *
     * @param data The data received with the remove event
     * @param subscribedPrinterId The printer server id the client is connected to. {@code null} if the client is connected to all printer servers.
     * @param socket The socket that received the remove event
     */
    internal fun onRemoveEventReceived(data: Array<Any>, subscribedPrinterId: String?, socket: SocketIoSocket) {
        log.info("Remove event received: $data")

        // Verify that we actually received a json
        if (data.isEmpty() || data.first() !is JSONObject) return onApplicationError(
            socket, code = "INVALID_REMOVE_DATA", message = "Invalid data received. Expected JSON object.",
        )

        val printerServerJson = data.first() as JSONObject

        //verify the printer server id was provided
        if (!printerServerJson.has("id")) {
            return onApplicationError(
                socket,
                code = "MISSING_ID",
                message = "Missing printer server id.",
            )
        }
        val printerServerId = printerServerJson.getString("id")

        // Check if the client is connected to a specific printer server and only allow changes to that printer server
        if (subscribedPrinterId != null && printerServerId != subscribedPrinterId) {
            return onApplicationError(
                socket,
                "PROVIDED_AND_SUBSCRIBED_ID_DO_NOT_MATCH",
                "Provided remove printer server id does not match the subscribed printer server id."
            )
        }

        // verify that the printer server actually exists and get it
        val printerServerDto = printerServerService.getPrinterServerById(printerServerId) ?: return onApplicationError(
            socket, "PRINTER_SERVER_NOT_FOUND", "Printer server with id $printerServerId not found.",
        )

        // Remove the printer server
        printerServerService.removePrinterServer(printerServerId)
        // Send removed data to subscribers
        onRemove(printerServerDto)
    }

    override fun onDisconnect(socket: SocketIoSocket) {
        val namespace = socket.namespace.name
        val printerServerId = getPrinterServerIdFromNamespace(namespace)

        if (printerServerId == null) {
            subscribersToAllPrinterServers.remove(socket)
        } else {
            subscribersToSpecificPrinterServer[printerServerId]?.remove(socket)
        }
    }

    override fun onRemove(data: PrinterServerDto) {
        val printerServerId = data.id
        val subscribersToSpecificPrinterServer = subscribersToSpecificPrinterServer[printerServerId] ?: emptySet()
        val subscribers = subscribersToAllPrinterServers + subscribersToSpecificPrinterServer
        subscribers.forEach { it.sendPojo(REMOVE_EVENT, data) }
    }

    override fun onChange(data: PrinterServerDto) {
        val printerServerId = data.id
        val subscribersToSpecificPrinterServer = subscribersToSpecificPrinterServer[printerServerId] ?: emptySet()
        val subscribers = subscribersToAllPrinterServers + subscribersToSpecificPrinterServer
        subscribers.forEach { it.sendPojo(CHANGE_EVENT, data) }
    }

    override fun onApplicationError(id: String?, error: ApplicationErrorDto) {
        if (id == null) {
            subscribersToAllPrinterServers.forEach { it.sendPojo(APPLICATION_ERROR_EVENT, error) }
            subscribersToSpecificPrinterServer.values.flatten().forEach { it.sendPojo(APPLICATION_ERROR_EVENT, error) }
        } else {
            subscribersToSpecificPrinterServer[id]?.forEach { it.sendPojo(APPLICATION_ERROR_EVENT, error) }
        }
    }

    private fun getPrinterServerIdFromNamespace(namespace: String): String? {
        val matchResult =
            namespacePattern.matchEntire(namespace) ?: throw IllegalArgumentException("Invalid namespace: $namespace")
        return matchResult.groups[1]?.value
    }
}