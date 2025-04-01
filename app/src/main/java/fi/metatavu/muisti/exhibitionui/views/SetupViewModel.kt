package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fi.metatavu.muisti.api.client.infrastructure.ClientException
import fi.metatavu.muisti.api.client.models.Device
import fi.metatavu.muisti.api.client.models.DeviceKey
import fi.metatavu.muisti.api.client.models.DeviceRequest
import fi.metatavu.muisti.api.client.models.DeviceType
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.DeviceSettingRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * View model for settings activity
 *
 * @param application application instance
 */
class SetupViewModel(application: Application): AndroidViewModel(application) {

    private val deviceSettingRepository: DeviceSettingRepository

    init {
        val deviceSettingDao = ExhibitionUIDatabase.getDatabase().deviceSettingDao()
        deviceSettingRepository = DeviceSettingRepository(deviceSettingDao)
    }

    /**
     * Returns device key if set
     *
     * @return device key or null if not set
     */
    suspend fun getDeviceKey(): String? {
        return DeviceSettings.getDeviceKey()
    }

    /**
     * Sets device key
     *
     * @param deviceKey identifier of the device
     */
    suspend fun setDeviceKey(deviceKey: String) = viewModelScope.launch {
        DeviceSettings.setDeviceKey(deviceKey)
    }

    /**
     * Returns device id if set
     *
     * @return device id or null if not set
     */
    suspend fun getDeviceId(): UUID? {
        return DeviceSettings.getDeviceId()
    }

    /**
     * Sets the device ID
     *
     * @param deviceId Device ID to set
     */
    suspend fun setDeviceId(deviceId: UUID) = viewModelScope.launch {
        DeviceSettings.setDeviceId(deviceId)
    }

    /**
     * Requests the device key
     *
     * @param deviceId device id
     * @return deviceKey or null if not set
     */
    suspend fun requestDeviceKey(deviceId: UUID): DeviceKey? {
        val deviceKey = getDeviceKey() ?: ""
        try {
            val devicesApi = MuistiApiFactory.getDevicesApi(deviceKey)
            return devicesApi.getDeviceKey(deviceId = deviceId)
        } catch (e: ClientException) {
            return null
        }
    }

    /**
     * Creates the device
     *
     * @return device
     */
    suspend fun createDevice(
        name: String,
        description: String?,
        serialNumber: String,
        deviceType: DeviceType,
        version: String
    ):  Device {
        val deviceKey = getDeviceKey() ?: ""
        val devicesApi = MuistiApiFactory.getDevicesApi(deviceKey)
        return devicesApi.createDevice(DeviceRequest(
            name = name,
            description = description,
            serialNumber = serialNumber,
            deviceType = deviceType,
            version = version
        ))
    }
}