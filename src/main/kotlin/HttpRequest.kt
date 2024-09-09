class HttpRequest(
    val method: String,
    val path: String,
    val httpVersion: String,
    val headers: Map<String, String>,
    val body: String?
) {
    override fun toString(): String {
        return "Method: $method\nPath: $path\nHTTP Version: $httpVersion\nHeaders: $headers\nBody: $body"
    }

    companion object {
        // Factory method to parse a raw HTTP request string into an HttpRequest object
        fun parse(request: String): HttpRequest {
            val lines = request.lines()
            val requestLine = lines[0].split(" ")
            val method = requestLine[0]
            val path = requestLine[1]
            val httpVersion = requestLine[2]

            // Headers start at line 1 and go until an empty line
            val headers = mutableMapOf<String, String>()
            var i = 1
            while (lines[i].isNotBlank()) {
                val header = lines[i].split(": ")
                headers[header[0].lowercase()] = header[1].lowercase()
                i++
            }

            // Optional body after headers
            val body = if (i < lines.size - 1) lines.subList(i + 1, lines.size).joinToString("\n") else null

            return HttpRequest(method, path, httpVersion, headers, body)
        }
    }
}
