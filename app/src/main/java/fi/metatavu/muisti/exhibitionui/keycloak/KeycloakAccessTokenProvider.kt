package fi.metatavu.muisti.exhibitionui.keycloak

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.exhibitionui.BuildConfig
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Keycloak access token provider
 */
class KeycloakAccessTokenProvider {

    /**
     * Returns an access token from Keycloak
     *
     * @return an access token from Keycloak or null if failed
     * @throws IOException on communication failure
     */
    fun getAccessToken() : KeycloakAccessToken? {
        try {
            Log.d(KeycloakAccessTokenProvider::javaClass.name, "Request fresh token...")

            val baseUrl = BuildConfig.KEYCLOAK_URL
            val realm = BuildConfig.KEYCLOAK_REALM
            val clientId = BuildConfig.KEYCLOAK_CLIENT_ID
            val username = BuildConfig.KEYCLOAK_USERNAME
            val password = BuildConfig.KEYCLOAK_PASSWORD
            val httpUrl = baseUrl.toHttpUrlOrNull() ?: throw IOException("baseUrl is invalid.")

            val url = httpUrl.newBuilder()
                .addPathSegments("realms/$realm/protocol/openid-connect/token")
                .build()

            val formBody = FormBody.Builder()
                .add("client_id", clientId)
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = OkHttpClient().newCall(request).execute()
            val token = response.body!!.string()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val jsonAdapter: JsonAdapter<KeycloakAccessToken> =
                moshi.adapter<KeycloakAccessToken>(KeycloakAccessToken::class.java)

            return jsonAdapter.fromJson(token)
        } catch (e: Exception) {
            Log.d(this.javaClass.name, "Failed to retrieve access token", e)
            return null
        }

    }
}