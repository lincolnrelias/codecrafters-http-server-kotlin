import java.net.ServerSocket;
import java.io.PrintWriter;
fun main() {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    println("Logs from your program will appear here!")
    
    // Uncomment this block to pass the first stage
    var serverSocket = ServerSocket(4221)
    val clientSocket = serverSocket.accept()
    println("accepted new connection")
    val output = PrintWriter(clientSocket.getOutputStream(), true)
    // Since the tester restarts your program quite often, setting SO_REUSEADDR
    // ensures that we don't run into 'Address already in use' errors
    serverSocket.reuseAddress = true
    val response = "HTTP/1.1 200 OK\r\n\r\n"
    output.println(response)
}
