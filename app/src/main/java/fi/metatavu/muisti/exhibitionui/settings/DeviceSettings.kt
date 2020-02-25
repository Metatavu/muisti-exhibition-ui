package fi.metatavu.muisti.exhibitionui.settings

import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSettingName
import fi.metatavu.muisti.exhibitionui.persistence.repository.DeviceSettingRepository
import java.util.*

/**
 * Class for handling device settings
 */
class DeviceSettings {

    companion object {

        private val deviceSettingRepository: DeviceSettingRepository =
            DeviceSettingRepository(ExhibitionUIDatabase.getDatabase().deviceSettingDao())

        /**
         * Returns exhibition id if set
         *
         * @return exhibition id or null if not set
         */
        suspend fun getExhibitionId(): UUID? {
            return getUUID(getSettingValue(DeviceSettingName.EXHIBITION_ID))
        }

        /**
         * Sets exhibition id
         *
         * @param exhibitionId exhibition id
         */
        suspend fun setExhibitionId(exhibitionId: UUID?) {
            setExhibitionId(exhibitionId?.toString())
        }

        /**
         * Sets exhibition id
         *
         * @param exhibitionId exhibition id
         */
        suspend fun setExhibitionId(exhibitionId: String?) {
            setSettingValue(DeviceSettingName.EXHIBITION_ID, exhibitionId)
        }

        /**
         * Returns exhibition device id if set
         *
         * @return exhibition device id or null if not set
         */
        suspend fun getExhibitionDeviceId(): UUID? {
            return getUUID(getSettingValue(DeviceSettingName.EXHIBITION_DEVICE_ID))
        }

        /**
         * Sets exhibition device id
         *
         * @param exhibitionDeviceId exhibition device id
         */
        suspend fun setExhibitionDeviceId(exhibitionDeviceId: String?) {
            setSettingValue(DeviceSettingName.EXHIBITION_DEVICE_ID, exhibitionDeviceId)
        }

        /**
         * Sets exhibition device id
         *
         * @param exhibitionDeviceId exhibition device id
         */
        suspend fun setExhibitionDeviceId(exhibitionDeviceId: UUID?) {
            setExhibitionDeviceId(exhibitionDeviceId?.toString())
        }

        /**
         * Returns a device setting value by name
         *
         * @param name device setting name
         * @return device setting value or null if not defined
         */
        private suspend fun getSettingValue(name: DeviceSettingName): String? {
            return deviceSettingRepository.getValue(name)
        }

        /**
         * Updates a device setting value by name
         *
         * @param name name
         * @param value new value
         */
        private suspend fun setSettingValue(name: DeviceSettingName, value: String?) {
            deviceSettingRepository.setValue(name, value)
        }

        /**
         * Returns an UUID from string
         *
         * @param string string representation
         * @return UUID or null if string is null
         */
        private fun getUUID(string: String?): UUID? {
            if (string != null) {
                return UUID.fromString(string)
            }

            return null
        }
    }
}