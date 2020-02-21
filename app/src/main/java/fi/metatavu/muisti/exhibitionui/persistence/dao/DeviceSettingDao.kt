package fi.metatavu.muisti.exhibitionui.persistence.dao

import androidx.room.*
import fi.metatavu.muisti.exhibitionui.persistence.model.DeviceSetting

/**
 * DAO class for DeviceSetting entity
 */
@Dao
interface DeviceSettingDao {

    /**
     * Inserts new setting into the database
     *
     * @param entity task entity
     */
    @Insert
    suspend fun insert(entity: DeviceSetting)

    /**
     * Finds a setting by name
     *
     * @param name setting
     * @return found setting or null if not found
     */
    @Query("SELECT * FROM DeviceSetting WHERE name = :name")
    suspend fun findByName(name: String): DeviceSetting?

    /**
     * Updates settings
     *
     * @param entities
     */
    @Update
    suspend fun update(vararg entities: DeviceSetting)

    /**
     * Deletes a setting
     *
     * @param entity
     */
    @Delete
    suspend fun delete(entity: DeviceSetting)

}