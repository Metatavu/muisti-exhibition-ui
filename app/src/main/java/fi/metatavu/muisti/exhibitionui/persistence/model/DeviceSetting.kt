package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Database entity for device settings
 *
 * @property name setting name
 * @property value setting value
 */
@Entity (indices = arrayOf(Index("name", unique = true)))
data class DeviceSetting (

    var name: String,

    var value: String

) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}