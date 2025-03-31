package fi.metatavu.muisti.exhibitionui.api

import fi.metatavu.muisti.api.client.apis.DeviceDataApi
import fi.metatavu.muisti.api.client.apis.DevicesApi
import fi.metatavu.muisti.api.client.infrastructure.ApiClient
import fi.metatavu.muisti.exhibitionui.BuildConfig

/**
 * Muisti API factory class
 */
class MuistiApiFactory {

    companion object {

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
    }
}