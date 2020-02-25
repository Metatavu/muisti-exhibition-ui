package fi.metatavu.muisti.exhibitionui.keycloak

import com.squareup.moshi.Json

/**
 * Moshi data class for Keycloak access tokens
 *
 * @property accessToken access token field value
 * @property expiresIn expires in field value
 * @property refreshExpiresIn refresh expires in field value
 * @property refreshToken refresh token field value
 * @property tokenType token type field value
 * @property notBeforePolicy not before policy field value
 * @property sessionState session state field value
 * @property scope scope field value
 */
data class KeycloakAccessToken (

    @Json(name = "access_token")
    val accessToken: String,

    @Json(name = "expires_in")
    val expiresIn: Long,

    @Json(name = "refresh_expires_in")
    val refreshExpiresIn: Long,

    @Json(name = "refresh_token")
    val refreshToken: String,

    @Json(name = "token_type")
    val tokenType: String,

    @Json(name = "not-before-policy")
    val notBeforePolicy: Long,

    @Json(name = "session_state")
    val sessionState: String,

    @Json(name = "scope")
    val scope: String

)