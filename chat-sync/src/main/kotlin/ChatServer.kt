import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

val clients = mutableListOf<Socket>()

fun main() {
    val server = ServerSocket(5555)
    println("Server started on port 5555")

    while (true) {
        val client = server.accept()
        clients.add(client)
        println("New connection: ${client.inetAddress.hostAddress}")

        thread {
            client.use {
                while (true) {
                    val message = client.getInputStream().bufferedReader().readLine() ?: break
                    println("Received: $message")
                    broadcast(message, client)
                }
            }
            clients.remove(client)
            println("Connection closed: ${client.inetAddress.hostAddress}")
        }
    }
}

fun broadcast(message: String, sender: Socket) {
    clients.forEach {
        if (it != sender) {
            it.getOutputStream().write((message + "\n").toByteArray())
        }
    }
}
