package fi.metatavu.muisti.exhibitionui.keycloak

import android.util.Log
import com.auth0.android.jwt.JWT
import java.lang.Exception
import java.time.OffsetDateTime

/**
 * Keycloak access token container
 */
class KeycloakAccessTokenContainer {

    companion object {

        private val expireSlack = 60L
        private var accessToken: KeycloakAccessToken? = null
        private var accessTokenExpires: OffsetDateTime? = null

        /**
         * Resolves a valid access token from Keycloak
         *
         * @return access token
         */
        fun getAccessToken(): KeycloakAccessToken? {
            try {
                synchronized(this) {
                    val now = OffsetDateTime.now()
                    val expires = accessTokenExpires?.minusSeconds(expireSlack)

                    if ((accessToken == null) || expires == null || expires.isBefore(now)) {
                        accessToken = KeycloakAccessTokenProvider().getAccessToken()
                        val expiresIn = accessToken?.expiresIn ?: return null
                        accessTokenExpires = OffsetDateTime.now().plusSeconds(expiresIn)
                    }

                    return accessToken
                }
            } catch (e: Exception) {
                Log.e(KeycloakAccessTokenContainer::javaClass.name, "Failed to retrieve access token", e)
            }

            return null
        }

        /**
         * Returns JWT token from Keycloak access token
         *
         * @return JWT token
         */
        fun getAccessTokenJWT(): JWT? {
            val accessToken = getAccessToken() ?: return null
            return JWT(accessToken.accessToken)
        }

    }
}