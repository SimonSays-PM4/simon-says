package ch.zhaw.pm4.simonsays.api.controller.socketio

import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDTO
import ch.zhaw.pm4.simonsays.utils.sendPojo
import io.socket.socketio.server.SocketIoSocket

/**
 * A namespace in socket.io is a way to separate the events that are handled by the server.
 * This is useful to separate different parts of the application and to avoid conflicts between events.
 *
 * **Every Bean that wants to handle namespace events needs to implement this interface.**
 *
 * In our case we separate the events very similar to the REST API where for example instead of doing
 *
 * GET rest-api/v1/printer-server/{id}/print-queues
 *
 * we can connect to
 *
 * socket-api/v1/printer-server/{id}/print-queues
 *
 * which would make "socket-api/v1/printer-server/{id}/print-queues" the namespace.
 */
interface SocketIoNamespace<K, T> {
    companion object {
        /** The change event name. */
        const val CHANGE_EVENT = "change"

        /** The initial data event name. */
        const val INITIAL_DATA_EVENT = "initial-data"

        /** The remove event name. */
        const val REMOVE_EVENT = "remove"

        /** The application error event name. */
        const val APPLICATION_ERROR_EVENT = "application-error"
    }

    /**
     * This method is called by the Socket.IO server to check if the given requested namespace name is part of the
     * namespace handled by this bean.
     * @param requestedNamespace The requested namespace name.
     * @return True if the requested namespace is part of the namespace handled by this bean, false otherwise.
     */
    fun isPartOfNamespace(requestedNamespace: String): Boolean

    /**
     * This method is called by the Socket.IO server to handle the connection event for the given namespace.
     * @param socket The socket that represents the connection.
     */
    fun onConnection(socket: SocketIoSocket)

    /**
     * This method is called by the Socket.IO server to handle the disconnection event for the given namespace.
     * @param socket The socket that represents the connection.
     */
    fun onDisconnect(socket: SocketIoSocket)

    /**
     * This method may be called when an object changes and needs to be sent to all connected clients.
     * @param data The data to send to all connected clients.
     */
    fun onChange(data: T)

    /**
     * This method is called when an object is removed and needs to be removed from all connected clients.
     * @param data The data to remove from all connected clients.
     */
    fun onRemove(data: T)

    /**
     * This method is called when an application error occurs and needs to be sent to all connected clients.
     *
     * @param id The unique identifier to send to (printer server id, job id, ...). When null, the error will be sent to all connected clients. Otherwise, only subscribers of the given id will receive the error.
     * @param error The error that occurred.
     */
    fun onApplicationError(id: K?, error: ApplicationErrorDTO)

    /**
     * Shorthand method for onApplicationError(id, ApplicationErrorDto(code, message)).
     */
    fun onApplicationError(id: K?, code: String, message: String) {
        val error = ApplicationErrorDTO(code, message)
        onApplicationError(id, error)
    }

    /**
     * Send and error to specific socket
     */
    fun onApplicationError(socket: SocketIoSocket, error: ApplicationErrorDTO) {
        socket.sendPojo(APPLICATION_ERROR_EVENT, error)
    }

    /**
     * Shorthand method for onApplicationError(socket, ApplicationErrorDto(code, message))
     */
    fun onApplicationError(socket: SocketIoSocket, code: String, message: String) {
        val error = ApplicationErrorDTO(code, message)
        onApplicationError(socket, error)
    }
}