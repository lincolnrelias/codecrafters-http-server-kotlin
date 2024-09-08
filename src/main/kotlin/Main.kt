import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.net.InetSocketAddress

fun main() {

    // Uncomment this block to pass the first stage
    val serverSocket = ServerSocket( 4221)
        val clientSocket = serverSocket.accept()
        println("accepted new connection")
        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val output = PrintWriter(clientSocket.getOutputStream(), true)
        // Since the tester restarts your program quite often, setting SO_REUSEADDR
        // ensures that we don't run into 'Address already in use' errors
        serverSocket.reuseAddress = true
        val splitResponse = input.readLine().split("/")
        var response ="HTTP/1.1 200 OK\r\n"

        if(splitResponse.size>3){
            if(splitResponse[1] == "echo"){
                response+="Content-Type: text/plain\r\nContent-Length: ${splitResponse[2].length}\r\n\r\n${splitResponse[2].split(" ")[0]}"
            }
        }
        output.println(response)
        input.close()
        output.close()
        clientSocket.close()


}
