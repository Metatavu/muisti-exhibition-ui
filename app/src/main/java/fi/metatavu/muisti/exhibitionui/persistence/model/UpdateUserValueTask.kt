package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for update user value task
 *
 * @property sessionId session id
 * @property time task create time
 * @property priority task priority
 * @property key user value key
 * @property value user value value
 */
@Entity
data class UpdateUserValueTask (

    override var sessionId: String,

    override var time: Long,

    override var priority: Long,

    var key: String,

    var value: String
): Task {

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0

}