package fi.metatavu.muisti.exhibitionui.services

import okhttp3.OkHttpClient
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import android.util.Log

class KeycloakAccessTokenProvider{

    fun getAccessToken() : String {
        Log.d(javaClass.name, "Retrieving access token")

        val baseUrl = ""

        val realm = ""

        val clientId= ""

        val username = ""

        val password = ""

        val httpUrl = baseUrl.toHttpUrlOrNull() ?: throw IllegalStateException("baseUrl is invalid.")

        val url = httpUrl.newBuilder()
            .addPathSegments("realms/$realm/protocol/openid-connect/token").build()

        val formBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("grant_type", "password")
            .add("username", username)
            .add("password", password)
            .build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        val response = client.newCall(request).execute()
        val token = response.body!!.string()
        Log.d(javaClass.name, "Received token: $token")

        return token
    }
}