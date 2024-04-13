package ch.zhaw.pm4.simonsays.testutils

import io.mockk.every
import io.mockk.mockk
import io.socket.socketio.server.SocketIoSocket

fun mockSocket(namespaceName: String? = null): SocketIoSocket {
    val mockSocket = mockk<SocketIoSocket>(relaxed = true)
    every { mockSocket.namespace } returns mockk {
        every { name } returns namespaceName
    }
    return mockSocket
}
