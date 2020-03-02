package fi.metatavu.muisti.exhibitionui.api

import fi.metatavu.muisti.api.client.apis.ExhibitionDevicesApi
import fi.metatavu.muisti.api.client.apis.ExhibitionPageLayoutsApi
import fi.metatavu.muisti.api.client.apis.ExhibitionsApi
import fi.metatavu.muisti.api.client.apis.VisitorSessionsApi
import fi.metatavu.muisti.api.client.infrastructure.ApiClient
import fi.metatavu.muisti.api.client.infrastructure.ClientException
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayout
import fi.metatavu.muisti.api.client.models.VisitorSessionUser
import fi.metatavu.muisti.exhibitionui.BuildConfig
import kotlinx.coroutines.delay

/**
 * Muisti API factory class
 */
class MuistiApiFactory {

    companion object {

        /**
         * Returns initialized visitor sessions API
         *
         * @return initialized visitor sessions API
         */
        suspend fun getVisitorSessionsApi(): VisitorSessionsApi {
            waitForToken()
            return VisitorSessionsApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Returns initialized exhibitions API
         *
         * @return initialized exhibitions API
         */
        suspend fun getExhibitionsApi(): ExhibitionsApi {
            waitForToken()
            return ExhibitionsApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Returns initialized exhibition devices API
         *
         * @return initialized exhibition devices API
         */
        suspend fun getExhibitionDevicesApi(): ExhibitionDevicesApi {
            waitForToken()
            return ExhibitionDevicesApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Returns initialized exhibition page layouts API
         *
         * @return initialized visitor sessions API
         */
        suspend fun exhibitionPageLayoutsApi(): ExhibitionPageLayoutsApi {
            waitForToken()
            return ExhibitionPageLayoutsApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Waits for API client to have an access token
         *
         * @throws ClientException when access token waiting times out
         */
        private suspend fun waitForToken() {
            val timeout = System.currentTimeMillis() + (1000 * 60 * 5)

            while (ApiClient.accessToken == null) {
                delay(500)

                if (System.currentTimeMillis() > timeout) {
                    throw ClientException("Timeout while waiting for access token")
                }
            }
        }

    }

}