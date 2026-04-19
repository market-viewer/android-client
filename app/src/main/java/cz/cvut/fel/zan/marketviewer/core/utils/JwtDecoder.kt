package cz.cvut.fel.zan.marketviewer.core.utils

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtDecoder {

    fun getUserId(token: String?): String? {
        if (token.isNullOrBlank()) return null

        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            // The payload is the second part (index 1)
            val payloadString = parts[1]

            // Decode the Base64 string
            val decodedBytes = Base64.decode(payloadString, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            // Parse the JSON and extract the username
            val jsonObject = JSONObject(decodedString)

            // Make sure this matches the exact key your backend uses!
            jsonObject.optString("sub")

        } catch (e: Exception) {
            null
        }
    }
}