import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket;
import java.io.PrintWriter;

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
    val requestLine = input.readLine().split("/")
    val headers = HashMap<String, String>()

    // Read and store headers
    var line: String?
    while (input.readLine().also { line = it } != null && line!!.isNotEmpty()) {
        println("Header: $line")
        // Split the header into key and value (name: value)
        val headerParts = line!!.split(":", limit = 2)
        if (headerParts.size == 2) {
            val headerName = headerParts[0].trim().lowercase()
            val headerValue = headerParts[1].trim().lowercase()
            headers[headerName] = headerValue
        }
    }
    var response ="HTTP/1.1 404 Not Found\r\n\r\n"

    if(requestLine[1]==" HTTP"){
            response ="HTTP/1.1 200 OK\r\n\r\n"
        } else if(requestLine.size>=3){
            when(requestLine[1].split(" ")[0]){
                "echo"->{
                    response ="HTTP/1.1 200 OK\r\n"
                    val term = requestLine[2].split(" ")[0]
                    response+="Content-Type: text/plain\r\nContent-Length: ${term.length}\r\n\r\n${term}"
                }
                "user-agent"->{
                    response ="HTTP/1.1 200 OK\r\n"
                    var uAgent = headers["user-agent"]
                    response+="Content-Type: text/plain\r\nContent-Length: ${uAgent?.length}\r\n\r\n${uAgent}"

                }
            }
        }
        output.println(response)
        input.close()
        output.close()
        clientSocket.close()


}
