import java.net.Socket
import java.util.UUID
import kotlin.concurrent.thread

fun main() {
    val client = Socket("127.0.0.1", 5555)
    println("Connected to the server")

    thread {
        client.use {
            val reader = it.getInputStream().bufferedReader()
            while (true) {
                val message = reader.readLine() ?: break
                println("Received: $message")
            }
        }
    }
    val userName = UUID.randomUUID().toString()
    val writer = client.getOutputStream().bufferedWriter()
    while (true) {
        val message = readLine() ?: break
        writer.write("$userName: $message")
        writer.newLine()
        writer.flush()
    }
}
