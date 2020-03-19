package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import fi.metatavu.muisti.exhibitionui.persistence.types.UUIDConverter
import java.util.*

/**
 * Database entity for update user value task
 *
 * @property sessionId session id
 * @property time task create time
 * @property priority task priority
 * @property name user value name
 * @property value user value value
 */
@Entity
data class UpdateUserValueTask (

    @TypeConverters(UUIDConverter::class)
    override var sessionId: UUID,

    override var time: Long,

    override var priority: Long,

    var name: String,

    var value: String
): Task {

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0

}