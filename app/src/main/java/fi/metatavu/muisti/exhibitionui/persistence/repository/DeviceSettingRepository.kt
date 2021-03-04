package fi.metatavu.muisti.exhibitionui.persistence.repository

import android.util.Log
import fi.metatavu.muisti.exhibitionui.persistence.dao.DeviceSettingDao
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSetting
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSettingName

/**
 * Repository class for DeviceSettings
 *
 * @property deviceSettingDao DeviceSetting DAO class
 */
class DeviceSettingRepository(private val deviceSettingDao: DeviceSettingDao) {

    /**
     * Returns a device setting value
     *
     * @param name name
     * @return a device setting value or null if not found
     */
    suspend fun getValue(name: DeviceSettingName): String? {
        val entity = deviceSettingDao.findByName(name.name)
        return entity?.value
    }

    /**
     * Sets a device setting value
     *
     * @param name name
     * @param value a device setting value. If null is specified setting is removed
     */
    suspend fun setValue(name: DeviceSettingName, value: String?) {
        val entity = deviceSettingDao.findByName(name.name)
        if (entity != null) {
            if (value != null) {
                entity.value = value
                deviceSettingDao.update(entity)
            } else {
                deviceSettingDao.delete(entity)
            }
        } else if (value != null) {
            deviceSettingDao.insert(DeviceSetting(name.name, value))
        }
    }
}