package fi.metatavu.muisti.exhibitionui.settings

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import fi.metatavu.muisti.api.client.infrastructure.Serializer.moshi
import fi.metatavu.muisti.api.client.models.RfidAntenna
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSettingName
import fi.metatavu.muisti.exhibitionui.persistence.repository.DeviceSettingRepository
import java.util.*

/**
 * Class for handling device settings
 */
class DeviceSettings {

    companion object {

        private var flipScreenRotate: Boolean? = null
        private var deviceSettingRepository: DeviceSettingRepository? = null
        private val listType = Types.newParameterizedType(MutableList::class.java, RfidAntenna::class.java)
        private val jsonAdapter: JsonAdapter<List<RfidAntenna>> = moshi.adapter<List<RfidAntenna>>(listType)

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
         * Sets rotation flip boolean setting
         *
         * @param value value of setting
         */
        suspend fun setRotationFlip(value: Boolean) {
            flipScreenRotate = value
            setSettingValue(DeviceSettingName.DEVICE_ROTATE_FLIP, value.toString())
        }

        /**
         * Gets rotation flip boolean setting
         *
         * @return Boolean value of the setting, defaults to false
         */
        suspend fun getRotationFlip(): Boolean {
            return flipScreenRotate ?: getSettingValue(DeviceSettingName.DEVICE_ROTATE_FLIP)?.toBoolean().also {
                flipScreenRotate = it
            } ?: false
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
         * Returns Rfid Antenna list or empty list
         *
         * @return list of Rfid Antennas
         */
        suspend fun getRfidAntennas(): List<RfidAntenna> {
            val rfidAntennas = jsonAdapter.fromJson(getSettingValue(DeviceSettingName.EXHIBITION_RFID_ANTENNA) ?: return emptyList())
            return rfidAntennas ?: emptyList()
        }

        /**
         * Weather or not the antenna is found in the current antenna list
         *
         * @param rfidAntenna antenna to look for
         * @return boolean
         */
        suspend fun hasRfidAntenna(rfidAntenna: RfidAntenna): Boolean {
            val rfidAntennas = jsonAdapter.fromJson(getSettingValue(DeviceSettingName.EXHIBITION_RFID_ANTENNA) ?: return false)
            return rfidAntennas?.any { it.id == rfidAntenna.id} ?: false
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
         * Sets exhibition antenna list
         *
         * @param rfidAntennaList to set into settings
         */
        suspend fun setExhibitionAntennaList(rfidAntennaList: List<RfidAntenna>) {
            val antennaJson = jsonAdapter.toJson(rfidAntennaList)
            setSettingValue(DeviceSettingName.EXHIBITION_RFID_ANTENNA, antennaJson)
        }

        /**
         * Sets rfid Antenna list to null
         */
        suspend fun removeAllExhibitionRfidAntennas() {
            setSettingValue(DeviceSettingName.EXHIBITION_RFID_ANTENNA, null)
        }

        /**
         * Returns a device setting value by name
         *
         * @param name device setting name
         * @return device setting value or null if not defined
         */
        private suspend fun getSettingValue(name: DeviceSettingName): String? {
            return getDeviceSettingRepository().getValue(name)
        }

        /**
         * Updates a device setting value by name
         *
         * @param name name
         * @param value new value
         */
        private suspend fun setSettingValue(name: DeviceSettingName, value: String?) {
            getDeviceSettingRepository().setValue(name, value)
        }

        /**
         * Returns device setting repository
         *
         * @return Device setting repository
         */
        private fun getDeviceSettingRepository(): DeviceSettingRepository {
            return deviceSettingRepository ?: DeviceSettingRepository(ExhibitionUIDatabase.getDatabase()
                .deviceSettingDao()).also {
                deviceSettingRepository = it
            }
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