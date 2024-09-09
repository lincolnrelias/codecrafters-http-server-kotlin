import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val directoryPath = if (args.isNotEmpty() && args[0] == "--directory") args[1] else ""
    val serverSocket = ServerSocket(4221)
    serverSocket.reuseAddress = true
    val threadPool = Executors.newFixedThreadPool(10)

    while (true) {
        val clientSocket = serverSocket.accept()
        threadPool.submit {
            try {
                val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                val outputStream = clientSocket.getOutputStream()

                // Read the full request and parse it
                val rawRequest = buildString {
                    var line: String?
                    while (input.readLine().also { line = it } != null && line!!.isNotEmpty()) {
                        append(line).append("\r\n")
                    }
                    // Check if there is a Content-Length header to read the body
                    val contentLength = this.lines().firstOrNull { it.startsWith("content-Length", ignoreCase = true) }
                        ?.split(":")?.get(1)?.trim()?.toIntOrNull()

                    // If there's a body (POST/PUT requests), read the content based on Content-Length
                    contentLength?.let {
                        append("\r\n")
                        val body = CharArray(it)
                        input.read(body, 0, it)
                        append(body)
                    }
                }

                val httpRequest = HttpRequest.parse(rawRequest)
                val httpResponse = handleRequest(httpRequest, directoryPath)
                httpResponse.sendResponse(outputStream)

                input.close()
                clientSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun handleRequest(httpRequest: HttpRequest, directoryPath: String): HttpResponse {
    return when {
        httpRequest.path == "/" ->{
            HttpResponse("200 OK")
        }
        // Handle echo endpoint
        httpRequest.path.startsWith("/echo") -> {
            val term = httpRequest.path.removePrefix("/echo/")
            val response = HttpResponse("200 OK")
            response.addHeader("content-Type", "text/plain")
            response.addHeader("content-Length", term.length.toString())
            response.body = term
            response
        }

        // Handle user-agent endpoint
        httpRequest.path.startsWith("/user-agent") -> {
            val userAgent = httpRequest.headers["user-agent"] ?: "Unknown"
            val response = HttpResponse("200 OK")
            response.addHeader("content-Type", "text/plain")
            response.addHeader("content-Length", userAgent.length.toString())
            response.body = userAgent
            response
        }

        // Handle files endpoint
        httpRequest.path.startsWith("/files") -> {
            val filename = httpRequest.path.removePrefix("/files/")
            val file = File(directoryPath, filename)
            if(httpRequest.method == "GET"){
                if (file.exists() && file.isFile) {
                    val fileContent = file.readText()
                    val response = HttpResponse("200 OK")
                    response.addHeader("content-Type", "application/octet-stream")
                    response.addHeader("content-Length", fileContent.length.toString())
                    response.body = fileContent
                    response
                } else {
                    HttpResponse("404 Not Found")
                }
            }else{
                try {
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                    }
                    if (httpRequest.body != null) {
                        file.writeText(httpRequest.body)
                        val response = HttpResponse("201 Created")
                        response.addHeader("content-Type", "application/octet-stream")
                        response.addHeader("content-Length", httpRequest.body.length.toString())
                        response.body = httpRequest.body
                        return response
                    }
                } catch (e:SecurityException){
                    e.printStackTrace()
                }

                HttpResponse("400 Bad Request")

            }


        }

        // Default 404 response for unknown paths
        else -> HttpResponse("404 Not Found")
    }
}
