package ch.zhaw.pm4.simonsays.utils.printer

import io.socket.socketio.server.SocketIoSocket
import org.json.JSONArray
import org.json.JSONObject

/**
 * Extension function to send a pojo as a JSON object to a socket.io socket.
 */
fun SocketIoSocket.sendPojo(event: String, data: Any?) {
    // if data is null, do not send anything
    if (data == null) {
        return
    }
    // For some reason socket.io does not support sending enums, so we have to convert them to strings
    val dataJson = replaceEnumsWithStrings(JSONObject(data))
    this.send(event, dataJson)
}

/**
 * Extension function to send a list of pojos as a JSON array to a socket.io socket.
 */
fun SocketIoSocket.sendPojo(event: String, data: List<Any>) {
    // For some reason socket.io does not support sending enums, so we have to convert them to strings
    val dataJson = replaceEnumsWithStrings(JSONArray(data))
    this.send(event, dataJson)
}

/**
 * Replace all enums with their string representation.
 */
private fun replaceEnumsWithStrings(data: JSONArray): JSONArray {
    for (i in 0 until data.length()) {
        val value = data[i]
        if (value is Enum<*>) {
            data.put(i, value.toString())
        } else {
            replaceEnumsWithStrings(value)
        }
    }
    return data
}

private fun replaceEnumsWithStrings(data: JSONObject): JSONObject {
    for (key in data.keys()) {
        val value = data[key]
        if (value is Enum<*>) {
            data.put(key, value.toString())
        } else {
            replaceEnumsWithStrings(value)
        }
    }
    return data
}

private fun replaceEnumsWithStrings(value: Any) {
    if (value is JSONArray) {
        replaceEnumsWithStrings(value)
    } else if (value is JSONObject) {
        replaceEnumsWithStrings(value)
    }
}