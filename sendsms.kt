import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun main() {
    val url = URL("https://api.umeskiasoftwares.com/api/v1/sms")
    val connection = url.openConnection() as HttpURLConnection

    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doOutput = true
    val requestBody = """
        {
            "api_key": "XXXXXXXXXXXXXXXXXX=", //Replace with your api key here
            "email": "example@gmail.com", //Replace with your email
            "Sender_Id": "23107", //If you have a custom sender id, use it here OR Use the default sender id: 23107
            "message": "UMS SMS Api Test Message",
            "phone": "0768XXXXX60" //Phone number should be in the format: 0768XXXXX60 OR 254768XXXXX60 OR 254168XXXXX60
        }
    """.trimIndent()

    val wr = DataOutputStream(connection.outputStream)
    wr.writeBytes(requestBody)
    wr.flush()
    wr.close()

    val responseCode = connection.responseCode

    if (responseCode == HttpURLConnection.HTTP_OK) {
        val response = StringBuffer()
        BufferedReader(InputStreamReader(connection.inputStream)).use { br ->
            var inputLine: String?
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
        }

        connection.disconnect()

        val newResponse = response.toString().replace("[", "").replace("]", "")
        val data = JSONObject(newResponse)
        val success = data.getString("success")
        if (success == "200") {
            val requestId = data.getString("request_id")
            val message = data.getString("message")
            println("Sms sent successfully, with request_id: $requestId and message: $message")
        } else {
            val resultCode = data.getString("ResultCode")
            val errorMessage = data.getString("errorMessage")
            println("Sms not sent, with ResultCode: $resultCode and errorMessage: $errorMessage")
        }
    } else {
        println("Request failed with status code $responseCode")
    }
}
