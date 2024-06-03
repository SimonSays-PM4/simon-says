package ch.zhaw.pm4.simonsays.api.controller.printer

import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.APPLICATION_ERROR_EVENT
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.CHANGE_EVENT
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.INITIAL_DATA_EVENT
import ch.zhaw.pm4.simonsays.api.controller.socketio.SocketIoNamespace.Companion.REMOVE_EVENT
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDTO
import ch.zhaw.pm4.simonsays.api.types.printer.JobStatusDTO
import ch.zhaw.pm4.simonsays.api.types.printer.PrintQueueJobDTO
import ch.zhaw.pm4.simonsays.service.printer.PrintQueueJobService
import ch.zhaw.pm4.simonsays.service.printer.PrintQueueService
import ch.zhaw.pm4.simonsays.service.printer.PrinterServerService
import ch.zhaw.pm4.simonsays.utils.sendPojo
import com.fasterxml.jackson.databind.ObjectMapper
import io.socket.socketio.server.SocketIoSocket
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

typealias PrinterServerId = String
typealias PrintQueueId = String
typealias PrintQueueJobId = String

@Component
class PrintQueueJobsNamespace(
    private val printerServerService: PrinterServerService,
    private val printQueueService: PrintQueueService,
    private val printQueueJobService: PrintQueueJobService,
    private val objectMapper: ObjectMapper,
) : SocketIoNamespace<String, PrintQueueJobDTO> {
    companion object {
        /**
         * Regex pattern to match the namespace. The first group is the printer server id, the second group is the print queue id and the third group is the job id.
         */
        internal val namespacePattern =
            "^/socket-api/v1/printer-servers/([\\w,-]+)/print-queues/([\\w,-]+)/jobs(?:/([\\w,-]+))?\$".toRegex()

        /** shorthand flag for the next job */
        const val NEXT_JOB = "next"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * A map of all subscribers that are interested in all print queue jobs of a specific print queue.
     * The key is the print queue id.
     */
    internal val subscribersToAllPrintQueueJobs = mutableMapOf<PrintQueueId, MutableSet<SocketIoSocket>>()

    /**
     * A map of all subscribers that are interested in the next pending job of a specific print queue.
     * The key is the print queue id.
     */
    internal val subscribersToNextPrintQueueJob = mutableMapOf<PrintQueueId, MutableSet<SocketIoSocket>>()

    /**
     * A map of all subscribers that are interested in specific jobs of a specific print queue.
     * The key is the job id.
     */
    internal val subscribersToSpecificPrintQueueJobs = mutableMapOf<PrintQueueJobId, MutableSet<SocketIoSocket>>()

    /**
     * A map of all queues and their current next pending print job
     */
    internal val nextPendingPrintQueueJobs = mutableMapOf<PrintQueueId, PrintQueueJobDTO?>()
    override fun isPartOfNamespace(requestedNamespace: String): Boolean {
        // check if matches the namespace pattern
        val matchResult = namespacePattern.matches(requestedNamespace)
        if (!matchResult) return false

        val (printerServerId, printQueueId, jobId) = getPrintQueueJobIdentifiersFromNamespace(requestedNamespace)
        // check that printer server exists
        if (!printerServerService.doesPrinterServerExist(printerServerId)) return false
        // check that print queue exists
        if (!printQueueService.doesPrintQueueExist(printQueueId)) return false

        // if job id is present, check that job exists
        if (jobId != null && jobId != NEXT_JOB && !printQueueJobService.doesPrintQueueJobExist(jobId)) return false

        return true
    }

    override fun onConnection(socket: SocketIoSocket) {
        val namespace = socket.namespace.name
        val (_, printQueueId, jobId) = getPrintQueueJobIdentifiersFromNamespace(namespace)

        // if the job id is not present, subscribe to all print queue jobs
        when (jobId) {
            null -> {
                val subscribers = subscribersToAllPrintQueueJobs.computeIfAbsent(printQueueId) { mutableSetOf() }
                subscribers.add(socket)
                val initialData = printQueueJobService.getAllPrintQueueJobsForPrintQueue(printQueueId)
                socket.sendPojo(INITIAL_DATA_EVENT, initialData)
            }

            NEXT_JOB -> {
                val subscribers = subscribersToNextPrintQueueJob.computeIfAbsent(printQueueId) { mutableSetOf() }
                subscribers.add(socket)
                val initialData = printQueueJobService.getNextPendingPrintQueueJob(printQueueId)
                socket.sendPojo(INITIAL_DATA_EVENT, initialData)
            }

            else -> {
                val subscribers = subscribersToSpecificPrintQueueJobs.computeIfAbsent(jobId) { mutableSetOf() }
                subscribers.add(socket)
                val initialData = printQueueJobService.getPrintQueueJobById(jobId)
                socket.sendPojo(INITIAL_DATA_EVENT, initialData)
            }
        }

        socket.on(CHANGE_EVENT) { data -> onChangeEventReceived(data, printQueueId, jobId, socket) }
        socket.on(REMOVE_EVENT) { data -> onRemoveEventReceived(data, jobId, socket) }
        socket.on(APPLICATION_ERROR_EVENT) { error -> log.warn("Received application error event: $error from socket ${socket.id} with namespace $namespace") }
    }

    /**
     * Invoked when a change event is received on the server
     *
     * @param data The data received with the change event
     * @param printQueueId The print queue id the client is connected to
     * @param subscribedJobId The job id the client is connected to. {@code null} if the client is connected to all print queue jobs.
     * @param socket The socket that received the change event
     */
    fun onChangeEventReceived(
        data: Array<Any>,
        printQueueId: PrintQueueId,
        subscribedJobId: PrintQueueJobId?,
        socket: SocketIoSocket,
    ) {
        log.info("Print queue job change event received: $data")

        // Verify that we actually received a json
        if (data.isEmpty() || data.first() !is JSONObject) {
            return onApplicationError(
                socket, code = "INVALID_CHANGE_DATA", message = "Invalid data received. Expected JSON object"
            )
        }
        val jobJson = data.first() as JSONObject

        // Get the printer server id if it was provided
        val jobId = if (jobJson.has("id")) jobJson.getString("id") else null

        // Check if the client is connected to a specific job and only allow changes to that print job
        if (subscribedJobId != null && subscribedJobId != NEXT_JOB && jobId != subscribedJobId) {
            return onApplicationError(
                socket,
                "PROVIDED_AND_SUBSCRIBED_ID_DO_NOT_MATCH",
                "Provided change job id $jobId does not match subscribed job id $subscribedJobId."
            )
        }

        // verify that if the id is set, it actually exists
        if (jobId != null && !printQueueJobService.doesPrintQueueJobExist(jobId)) {
            return onApplicationError(
                socket, "PRINT_QUEUE_JOB_NOT_FOUND", "Printer server with id $jobId not found."
            )
        }

        savePrintQueueJob(socket, printQueueId, jobId, jobJson)
    }

    fun savePrintQueueJob(
        socket: SocketIoSocket,
        printQueueId: PrintQueueId,
        jobId: PrintQueueJobId?,
        jobJson: JSONObject
    ) {
        // set the last update date
        val currentTimeMillis = System.currentTimeMillis()
        jobJson.put("lastUpdateDateTime", currentTimeMillis)

        // if the id is not set, we generate a new one, so we create a new entry
        val printQueueJobDto = if (jobId == null) {
            jobJson.put("id", UUID.randomUUID())
            // set/override creation date
            jobJson.put("creationDateTime", currentTimeMillis)
            // Attempt to convert json to data class
            val printQueueJobDto = try {
                objectMapper.convertValue(jobJson, PrintQueueJobDTO::class.java)
            } catch (error: Error) {
                return onApplicationError(
                    socket,
                    "INVALID_CHANGE_DATA",
                    "Invalid or malformed data received. Expected a valid print queue job."
                )
            }
            printQueueJobDto
        } else {
            // if the id is set, we only allow changes to the job status and status message
            val existingPrintQueueJob = printQueueJobService.getPrintQueueJobById(jobId) ?: return onApplicationError(
                socket, "PRINT_QUEUE_JOB_NOT_FOUND", "Print queue job with id $jobId not found."
            )
            val updatedPrintQueueJobDto = existingPrintQueueJob.copy(
                status = if (jobJson.has("status")) JobStatusDTO.valueOf(jobJson.getString("status")) else existingPrintQueueJob.status,
                statusMessage = if (jobJson.has("statusMessage")) jobJson.getString("statusMessage") else existingPrintQueueJob.statusMessage,
            )
            // warn client if they want to change something else than status or status message
            if (jobJson.keys().asSequence()
                    .any { it != "status" && it != "statusMessage" && it != "id" && it != "lastUpdateDateTime" }
            ) {
                onApplicationError(
                    socket,
                    "INVALID_CHANGE_DATA",
                    "Partially invalid or change data received. Only status and status message can be changed. Any other value will be ignored."
                )
            }

            updatedPrintQueueJobDto
        }

        try {
            val savedPrintQueueJob = printQueueJobService.savePrintQueueJob(printQueueId, printQueueJobDto)
            // Send updated data to subscribers
            onChange(savedPrintQueueJob)
        } catch (error: Exception) {
            return onApplicationError(
                socket, "SAVE_ERROR", "Error saving print queue job: ${error.message}"
            )
        }
    }

    /**
     * Invoked when a remove event is received on the server
     *
     * @param data The data received with the remove event
     * @param subscribedJobId The job id the client is connected to. {@code null} if the client is connected to all print queue jobs.
     * @param socket The socket that received the remove event
     */
    internal fun onRemoveEventReceived(
        data: Array<Any>,
        subscribedJobId: PrintQueueJobId?,
        socket: SocketIoSocket,
    ) {
        log.info("Remove event received: $data")

        // Verify that we actually received a json
        if (data.isEmpty() || data.first() !is JSONObject) {
            return onApplicationError(
                socket, code = "INVALID_REMOVE_DATA", message = "Invalid data received. Expected JSON object"
            )
        }
        val jobJson = data.first() as JSONObject

        // verify the job id was provided
        if (!jobJson.has("id")) {
            return onApplicationError(
                socket, "MISSING_ID", "Missing print queue job id in remove data."
            )
        }

        // Get the job id
        val jobId = jobJson.getString("id")

        // Check if the client is connected to a specific job and only allow changes to that print job
        if (subscribedJobId != null && jobId != subscribedJobId) {
            return onApplicationError(
                socket,
                "PROVIDED_AND_SUBSCRIBED_ID_DO_NOT_MATCH",
                "Provided remove job id $jobId does not match subscribed job id $subscribedJobId."
            )
        }

        // verify that the job actually exists and get it
        val removedPrintQueueJob = printQueueJobService.getPrintQueueJobById(jobId) ?: return onApplicationError(
            socket, "PRINT_QUEUE_JOB_NOT_FOUND", "Print queue job with id $jobId not found."
        )

        // Remove the print queue job
        printQueueJobService.removePrintQueueJob(jobId)
        // Send updated data to subscribers
        onRemove(removedPrintQueueJob)
    }


    override fun onDisconnect(socket: SocketIoSocket) {
        val namespace = socket.namespace.name
        val (_, printQueueId, jobId) = getPrintQueueJobIdentifiersFromNamespace(namespace)

        // if the job id is not present, unsubscribe from all print queue jobs
        when (jobId) {
            null -> {
                val subscribers = subscribersToAllPrintQueueJobs[printQueueId]
                subscribers?.remove(socket)
            }

            NEXT_JOB -> {
                val subscribers = subscribersToNextPrintQueueJob[printQueueId]
                subscribers?.remove(socket)
            }

            else -> {
                val subscribers = subscribersToSpecificPrintQueueJobs[jobId]
                subscribers?.remove(socket)
            }
        }
    }

    override fun onApplicationError(id: PrintQueueJobId?, error: ApplicationErrorDTO) {
        val subscribers = if (id == null) {
            subscribersToAllPrintQueueJobs.values.flatten() + subscribersToNextPrintQueueJob.values.flatten() + subscribersToSpecificPrintQueueJobs.values.flatten()
        } else {
            val onErrorJobSubscribers = subscribersToSpecificPrintQueueJobs[id] ?: emptySet()
            // check all next pending jobs if they have the same job id and add the subscribers to the list
            subscribersToAllPrintQueueJobs.values.flatten() + onErrorJobSubscribers + getAffectedNextPrintJobSubscribers(id)
        }
        subscribers.forEach { it.sendPojo(APPLICATION_ERROR_EVENT, error) }
    }

    override fun onRemove(data: PrintQueueJobDTO) {
        val jobId = data.id
        val printQueueId = printQueueJobService.getPrintQueueIdForJob(jobId)
        val subscribers =
            (subscribersToAllPrintQueueJobs[printQueueId] ?: emptySet()) + // all subscribers to the print queue
                    (subscribersToSpecificPrintQueueJobs[jobId] ?: emptySet()) +  // all subscribers to the specific job
                    getAffectedNextPrintJobSubscribers(jobId, printQueueId) // all subscribers to the next job
        subscribers.forEach { it.sendPojo(REMOVE_EVENT, data) }
    }

    override fun onChange(data: PrintQueueJobDTO) {
        val jobId = data.id
        val printQueueId = try {
            printQueueJobService.getPrintQueueIdForJob(jobId)
        } catch (e: Exception) {
            return onApplicationError(
                jobId, ApplicationErrorDTO(
                    "UNABLE_TO_FIND_PRINT_QUEUE_ID", "Unable to find print queue id for job $jobId. Error: ${e.message}"
                )
            )
        }

        // Notify all queue subscribers
        subscribersToAllPrintQueueJobs[printQueueId]?.forEach { it.sendPojo(CHANGE_EVENT, data) }

        // Notify all specific job subscribers
        subscribersToSpecificPrintQueueJobs[jobId]?.forEach { it.sendPojo(CHANGE_EVENT, data) }

        // If the next pending job has changed we need to send the new job
        val updatedNextPendingJob = printQueueJobService.getNextPendingPrintQueueJob(printQueueId)
        nextPendingPrintQueueJobs[printQueueId] = updatedNextPendingJob
        // If the next pending job has not changed, we still want to send the updated job
        subscribersToNextPrintQueueJob[printQueueId]?.forEach { it.sendPojo(CHANGE_EVENT, updatedNextPendingJob) }
    }

    /**
     * Get all subscribers that are interested in the next print job
     */
    private fun getAffectedNextPrintJobSubscribers(jobId: PrintQueueJobId): Set<SocketIoSocket> {
        val printQueueId = printQueueJobService.getPrintQueueIdForJob(jobId)
        return getAffectedNextPrintJobSubscribers(jobId, printQueueId)
    }

    /**
     * Get all subscribers that are interested in the next print job
     */
    private fun getAffectedNextPrintJobSubscribers(
        jobId: PrintQueueJobId,
        printQueueId: PrintQueueId,
    ): Set<SocketIoSocket> {
        if (jobId == nextPendingPrintQueueJobs[printQueueId]?.id) {
            return subscribersToNextPrintQueueJob[printQueueId] ?: emptySet()
        }
        return emptySet()
    }

    private fun getPrintQueueJobIdentifiersFromNamespace(namespace: String): Triple<PrinterServerId, PrintQueueId, PrintQueueJobId?> {
        // the namespace should match the pattern since we already check that on connect. Hence, the following error should never occur.
        val matchResult =
            namespacePattern.matchEntire(namespace) ?: throw IllegalArgumentException("Invalid namespace: $namespace")
        return Triple(
            matchResult.groups[1]?.value ?: throw IllegalArgumentException("Invalid namespace: $namespace"),
            matchResult.groups[2]?.value ?: throw IllegalArgumentException("Invalid namespace: $namespace"),
            matchResult.groups[3]?.value
        )
    }
}