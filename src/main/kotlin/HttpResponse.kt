
class HttpResponse(
    var statusCode: String,
    var headers: MutableMap<String, String> = mutableMapOf(),
    var body: String? = null
) {
    fun addHeader(name: String, value: String) {
        headers[name] = value
    }

    fun toResponseString(): String {
        val response = StringBuilder()

        // Status line
        response.append("HTTP/1.1 $statusCode\r\n")

        // Headers
        headers.forEach { (key, value) ->
            response.append("$key: $value\r\n")
        }

        // Blank line between headers and body
        response.append("\r\n")

        return response.toString()
    }

    fun sendResponse(outputStream: java.io.OutputStream) {
        // Write the response string (status line + headers)
        outputStream.write(toResponseString().toByteArray())

        // Write the response body (if any)
        body?.let {
            outputStream.write(it.toByteArray())
        }
        outputStream.flush()
    }
}
