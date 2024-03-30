package ch.zhaw.pm4.simonsays.utils.printer

import io.socket.socketio.server.SocketIoSocket
import org.json.JSONArray
import org.json.JSONObject

/**
 * Extension function to send a pojo as a JSON object to a socket.io socket.
 */
fun SocketIoSocket.sendPojo(event: String, data: Any?) {
    if (data == null) {
        this.send(event, null)
        return
    }
    val dataJson = JSONObject(data)
    this.send(event, dataJson)
}

/**
 * Extension function to send a list of pojos as a JSON array to a socket.io socket.
 */
fun SocketIoSocket.sendPojos(event: String, data: List<Any>) {
    val dataJson = JSONArray(data)
    this.send(event, dataJson)
}