package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import fi.metatavu.muisti.api.client.models.Exhibition
import fi.metatavu.muisti.api.client.models.ExhibitionDevice
import fi.metatavu.muisti.api.client.models.RfidAntenna
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSettingName
import fi.metatavu.muisti.exhibitionui.persistence.repository.DeviceSettingRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.launch
import java.util.*

/**
 * View model for settings activity
 *
 * @constructor
 * model constructor
 *
 * @param application application instance
 */
class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private val deviceSettingRepository: DeviceSettingRepository

    init {
        val deviceSettingDao = ExhibitionUIDatabase.getDatabase().deviceSettingDao()
        deviceSettingRepository = DeviceSettingRepository(deviceSettingDao)
    }

    /**
     * Lists all exhibitions from API
     *
     * @return all exhibitions from API
     */
    suspend fun listExhibitions(): Array<Exhibition> {
        return MuistiApiFactory.getExhibitionsApi().listExhibitions()
    }

    /**
     * Lists exhibition devices from API
     *
     * @param exhibitionId exhibition id of which devices are being listed
     * @param exhibitionGroupId filter results by group id. Ignored when null specified
     * @return exhibition devices
     */
    suspend fun listExhibitionDevices(exhibitionId: UUID, exhibitionGroupId: UUID?): Array<ExhibitionDevice> {
        return MuistiApiFactory.getExhibitionDevicesApi().listExhibitionDevices(exhibitionId, exhibitionGroupId)
    }

    /**
     * Returns exhibition id if set
     *
     * @return exhibition id or null if not set
     */
    suspend fun getExhibitionId(): UUID? {
        return DeviceSettings.getExhibitionId()
    }

    /**
     * Sets exhibition id
     *
     * @param exhibitionId exhibition id
     */
    fun setExhibitionId(exhibitionId: String?) = viewModelScope.launch {
        DeviceSettings.setExhibitionId(exhibitionId)
    }

    /**
     * Sets rotation flip setting
     *
     * @param value setting value
     */
    fun setRotationFlip(value: Boolean) = viewModelScope.launch {
        DeviceSettings.setRotationFlip(value)
    }

    /**
     * Returns exhibition device id if set
     *
     * @return exhibition device id or null if not set
     */
    suspend fun getExhibitionDeviceId(): UUID? {
        return DeviceSettings.getExhibitionDeviceId()
    }

    /**
     * Returns Rfid antennas from device settings
     *
     * @return List of Rfid Antennas
     */
    suspend fun getRfidAntennas(): List<RfidAntenna> {
        return DeviceSettings.getRfidAntennas()
    }


    /**
     * Sets exhibition device id
     *
     * @param exhibitionDeviceId exhibition device id
     */
    fun setExhibitionDeviceId(exhibitionDeviceId: String?) = viewModelScope.launch {
        DeviceSettings.setExhibitionDeviceId(exhibitionDeviceId)
    }
}