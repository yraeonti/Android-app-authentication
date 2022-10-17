package com.auth.app.utils


import android.util.Log
import org.json.JSONException
import org.json.JSONObject

object SimplifiedMessage {
    fun get(stringMessage: String): HashMap<String, String> {
        val messages = HashMap<String, String>()
        val jsonObject = JSONObject(stringMessage)

        try {
            val jsonMessages = jsonObject.getJSONObject("message")
            jsonMessages.keys().forEach { messages[it] = jsonMessages.getString(it) }
        } catch (e: JSONException) {
            messages["message"] = jsonObject.getString("message")
        }
        Log.d("pint", messages.toString())
        for (key in messages.keys) {
            println("Element at key $key = ${messages[key]}")
        }
        return messages
    }
}