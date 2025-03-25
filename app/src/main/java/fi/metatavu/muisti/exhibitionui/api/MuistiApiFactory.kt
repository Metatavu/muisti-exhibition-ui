package fi.metatavu.muisti.exhibitionui.api

import fi.metatavu.muisti.api.client.apis.*
import fi.metatavu.muisti.api.client.infrastructure.ApiClient
import fi.metatavu.muisti.api.client.infrastructure.ClientException
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
         * Returns initialized visitors API
         *
         * @return initialized visitors API
         */
        suspend fun getVisitorsApi(): VisitorsApi {
            waitForToken()
            return VisitorsApi(BuildConfig.MUISTI_API_BASE_URL)
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
         * Returns initialized exhibition pages API
         *
         * @return initialized exhibition pages API
         */
        suspend fun getRfidAntennaApi(): RfidAntennasApi {
            waitForToken()
            return RfidAntennasApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Returns initialized device data API
         *
         * @return initialized device data API
         */
        fun getDeviceDataApi(deviceKey: String): DeviceDataApi {
            ApiClient.apiKey["X-DEVICE-KEY"] = deviceKey
            return DeviceDataApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Returns initialized devices API
         *
         * @return initialized devices API
         */
        fun getDevicesApi(deviceKey: String): DevicesApi {
            ApiClient.apiKey["X-DEVICE-KEY"] = deviceKey
            return DevicesApi(BuildConfig.MUISTI_API_BASE_URL)
        }

        /**
         * Waits for API client to have an access token
         *
         * @throws ClientException when access token waiting times out
         */
        @Throws(ClientException::class)
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