import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

// 방 이름을 키로, 클라이언트 리스트를 값으로 가지는 Map
val rooms = mutableMapOf<String, MutableList<Socket>>()

fun main() {
    val port = 5555
    val server = ServerSocket(port)
    println("Server started on port $port")

    while (true) {
        val client = server.accept()
        println("New connection: ${client.inetAddress.hostAddress}")

        thread {
            client.use {
                // 클라이언트가 참여할 방을 결정
                val roomName = client.getInputStream().bufferedReader().readLine()
                joinRoom(roomName, client)
                handleClient(client, roomName)
            }
        }
    }
}

fun joinRoom(roomName: String, client: Socket) {
    val clientsInRoom = rooms.getOrPut(roomName) { mutableListOf() }
    clientsInRoom.add(client)
    println("${client.inetAddress.hostAddress} joined $roomName")
}

fun leaveRoom(roomName: String, client: Socket) {
    val clientsInRoom = rooms[roomName]
    clientsInRoom?.remove(client)
    println("${client.inetAddress.hostAddress} left $roomName")

    // 방이 비어 있으면 제거
    if (clientsInRoom?.isEmpty() == true) {
        rooms.remove(roomName)
        println("Room $roomName is empty and has been removed")
    }
}

fun handleClient(client: Socket, roomName: String) {
    try {
        client.use {
            while (true) {
                val message = client.getInputStream().bufferedReader().readLine() ?: break
                println("Received from $roomName: $message")
                broadcast(message, client, roomName)
            }
        }
    } catch (e: IOException) {
        println("Connection lost: ${client.inetAddress.hostAddress}")
    } finally {
        leaveRoom(roomName, client)
    }
}

fun broadcast(message: String, sender: Socket, roomName: String) {
    val clientsInRoom = rooms[roomName]
    clientsInRoom?.forEach {
        if (it != sender) {
            try {
                it.getOutputStream().write((message + "\n").toByteArray())
            } catch (e: IOException) {
                println("Failed to send message to ${it.inetAddress.hostAddress}")
                leaveRoom(roomName, it)
            }
        }
    }
}