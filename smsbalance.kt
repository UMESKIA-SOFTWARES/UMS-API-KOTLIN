import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun main() {
    val url = URL("https://api.umeskiasoftwares.com/api/v1/smsbalance")
    val connection = url.openConnection() as HttpURLConnection

    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doOutput = true
    val requestBody = """
        {
            "api_key": "XXXXXXXXXXXXXXX=", //Replace with your api key here
            "email": "example@gmail.com" //Replace with your email
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

        val data = JSONObject(response.toString())
        val success = data.getString("success")
        if (success == "200") {
            val creditBalance = data.getString("creditBalance")
            println("Sms Balance retrieved successfully, with creditBalance: $creditBalance")
        } else {
            val resultCode = data.getString("ResultCode")
            val errorMessage = data.getString("errorMessage")
            println("Sms not sent, with ResultCode: $resultCode and errorMessage: $errorMessage")
        }
    } else {
        println("Request failed with status code $responseCode")
    }
}
