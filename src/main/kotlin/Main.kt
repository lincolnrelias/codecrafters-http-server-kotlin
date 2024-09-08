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
        var response ="HTTP/1.1 404 Not Found\r\n\r\n"
        if(splitResponse.size>3){
            if(splitResponse[1] == "echo"){
                response ="HTTP/1.1 200 OK\r\n"
                val term = splitResponse[2].split(" ")[0]
                response+="Content-Type: text/plain\r\nContent-Length: ${term.length}\r\n\r\n${term}"
            }
        }else if(splitResponse[1]==" HTTP"){
            response ="HTTP/1.1 200 OK\r\n\r\n"
        }
        output.println(response)
        input.close()
        output.close()
        clientSocket.close()


}
