package fi.metatavu.muisti.exhibitionui.persistence.types

import androidx.room.TypeConverter
import java.util.*

class UUIDConverter {

    /**
     * Returns UUID from String
     *
     * @param data json data of
     * @return Returns null if data is null else returns UUID
     */
    @TypeConverter
    fun stringToUUID(data: String?): UUID? {
        data?:return null
        return UUID.fromString(data)
    }

    /**
     * Returns a String from UUID
     *
     * @param uuid UUID to be converted
     * @return UUID as String if UUID is not null
     */
    @TypeConverter
    fun uuidToString(uuid: UUID?): String? {
        return uuid?.toString()
    }
}