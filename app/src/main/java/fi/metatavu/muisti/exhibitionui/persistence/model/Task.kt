package fi.metatavu.muisti.exhibitionui.persistence.model

import java.util.*

/**
 * Interface that describe all common parts of a database persisted task
 */
interface Task {

    /**
     * Entity id in database
     */
    var id: Long

    /**
     * Session id
     */
    var sessionId: UUID

    /**
     * Task create time
     */
    var time: Long

    /**
     * Task priority
     */
    var priority: Long

}