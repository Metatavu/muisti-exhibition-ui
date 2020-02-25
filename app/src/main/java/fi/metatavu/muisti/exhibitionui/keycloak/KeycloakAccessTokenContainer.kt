package fi.metatavu.muisti.exhibitionui.keycloak

import android.util.Log
import com.auth0.android.jwt.JWT
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
            synchronized(this) {
                val expireTime = OffsetDateTime.now().minusSeconds(expireSlack)

                if ((accessToken == null) || (accessTokenExpires == null) || (accessTokenExpires!!.isBefore(expireTime))) {
                    accessToken = KeycloakAccessTokenProvider().getAccessToken()
                    val expiresIn = accessToken?.expiresIn ?: return null
                    accessTokenExpires = OffsetDateTime.now().plusSeconds(expiresIn)
                }

                return accessToken
            }
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