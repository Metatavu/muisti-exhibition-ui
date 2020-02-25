package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.infrastructure.ApiClient
import fi.metatavu.muisti.exhibitionui.keycloak.KeycloakAccessTokenContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Service for keeping Keycloak access token up-to-date
 */
class UpdateKeycloakTokenService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            val accessToken = KeycloakAccessTokenContainer.getAccessToken()
            if (accessToken != null) {
                ApiClient.accessToken = accessToken.accessToken
            }
        }
    }

}