package ch.zhaw.pm4.simonsays.api.controller.printer

import ch.zhaw.pm4.simonsays.api.controller.SocketIoNamespace
import ch.zhaw.pm4.simonsays.service.printer.PrintQueueService
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import ch.zhaw.pm4.simonsays.utils.printer.sendPojos
import io.socket.socketio.server.SocketIoSocket
import org.springframework.stereotype.Controller

typealias PrinterServerId = String
typealias PrintQueueId = String

/**
 * This class is responsible for handling the '/socket-api/v1/printer-server/{id}/print-queues' and
 * '/socket-api/v1/printer-server/{id}/print-queues/{id}' namespaces.
 */
@Controller
class PrinterQueueNamespace(
    private val printQueueService: PrintQueueService,
) : SocketIoNamespace {
    companion object {
        /**
         * Regex pattern to match the namespace. The first group is the printer server id and the second group
         * is the print queue id. The print queue id is optional/may be empty.
         */
        val namespacePattern = "^/socket-api/v1/printer-server/([\\w,-]+)/print-queues/?([\\w,-]+)?\$".toRegex()
    }

    /**
     * A set of all subscribers that are interested in all print queues of a specific printer server. The key is the
     * printer server id.
     */
    val subscribersToAllPrintQueues = mutableMapOf<PrinterServerId, MutableSet<SocketIoSocket>>()

    /**
     * A map of all subscribers that are interested in specific print queues of a specific printer server. The key is
     * the printer server id. The value is a map where the key is the print queue id and the value is a set of sockets
     */
    val subscribersToSpecificPrintQueues =
        mutableMapOf<PrinterServerId, MutableMap<PrintQueueId, MutableSet<SocketIoSocket>>>()

    override fun isPartOfNamespace(requestedNamespace: String): Boolean {
        // TODO(Lukas): Check if printer server actually exists in database and if specified print queue exists.
        return namespacePattern.matches(requestedNamespace)
    }

    override fun onConnection(socket: SocketIoSocket) {
        val namespace = socket.namespace.name
        val (printerServerId, printQueueId) = computeIds(namespace)

        if (printQueueId == null) {
            val printServerSubscribers = subscribersToAllPrintQueues.computeIfAbsent(printerServerId) { mutableSetOf() }
            printServerSubscribers.add(socket)
            val initialData = printQueueService.getAllPrintQueuesForPrintServer(printerServerId)
            socket.sendPojos("initial-data", initialData)
        } else {
            val printServerSubscribers =
                subscribersToSpecificPrintQueues.computeIfAbsent(printerServerId) { mutableMapOf() }
            printServerSubscribers.computeIfAbsent(printQueueId) { mutableSetOf() }.add(socket)
            val initialData = printQueueService.getPrintQueueById(printerServerId, printQueueId)
            socket.sendPojo("initial-data", initialData)
        }
    }

    override fun onDisconnection(socket: SocketIoSocket) {
        val namespace = socket.namespace.name
        val (printerServerID, printQueueID) = computeIds(namespace)

        if (printQueueID == null) {
            subscribersToAllPrintQueues[printerServerID]?.remove(socket)
        } else {
            subscribersToSpecificPrintQueues[printerServerID]?.get(printQueueID)?.remove(socket)
        }
    }

    private fun computeIds(namespace: String): Pair<PrinterServerId, PrintQueueId?> {
        val matchResult = namespacePattern.matchEntire(namespace)
            ?: // This should never happen as the namespace should be checked before.
            throw IllegalStateException("Invalid namespace: $namespace")
        val printerServerId = matchResult.groups[1]?.value
            ?: // This should never happen because the regex pattern requires a printer server id.
            throw IllegalStateException("No printer server id found in namespace: $namespace")
        val printQueueId = matchResult.groups[2]?.value
        return Pair(printerServerId, printQueueId)
    }
}

