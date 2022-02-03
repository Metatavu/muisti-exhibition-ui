package fi.metatavu.muisti.exhibitionui.visitors

import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSessionV2
import java.time.OffsetDateTime

/**
 * Container for exhibition visitors
 */
class ExhibitionVisitorsContainer {

    companion object {

        private var visitors: Array<Visitor> = arrayOf()

        private var visitorSessions: List<VisitorSessionV2> = listOf()

        /**
         * Sets visitors
         *
         * @param updatedVisitorsList visitors to set
         */
        fun setVisitors(updatedVisitorsList: Array<Visitor>) {
            visitors = updatedVisitorsList
        }

        /**
         * Removes expired visitor sessions from visitor session list
         *
         * @return returns count of removed sessions
         */
        fun removeExpiredVisitorSessions(): Int {
            val updatedVisitorSessions = visitorSessions.filter { visitorSession ->
                OffsetDateTime.now().isBefore(OffsetDateTime.parse(visitorSession.expiresAt))
            }

            val count = visitorSessions.size - updatedVisitorSessions.size
            visitorSessions = updatedVisitorSessions
            return count
        }

        /**
         * Adds new visitor sessions to visitor sessions list.
         *
         * Duplicates are removed automatically
         *
         * @param newVisitorSessions new visitor sessions
         * @return updated visitor session list
         */
        fun addVisitorSessions(newVisitorSessions: Array<VisitorSessionV2>): List<VisitorSessionV2> {
            visitorSessions = visitorSessions
                .plus(newVisitorSessions)
                .distinctBy(VisitorSessionV2::id)

            return visitorSessions
        }

        /**
         * Updates existing visitor session on visitor session list
         *
         * @param visitorSession visitor session
         */
        fun updateVisitorSession(visitorSession: VisitorSessionV2) {
            visitorSessions = visitorSessions
                .filter { it.id != visitorSession.id }
                .plus(visitorSession)
        }

        /**
         * Finds a visitor by tag
         *
         * @param tag tag to find visitor with
         * @return Visitor or null
         */
        fun findVisitorByTag(tag: String): Visitor? {
            return visitors.firstOrNull{ it.tagId == tag }
        }

        /**
         * Finds a visitor session by the specified tag
         *
         * @param tag Tag to find visitor session with
         * @return Visitor Session or null
         */
        fun findVisitorSessionByTag(tag: String): VisitorSessionV2? {
            return visitorSessions.find { visitorSession ->
                visitorSession.tags?.contains(tag) ?: false
            }
        }

        /**
         * Returns a Visitor session that contains all the specified tags
         *
         * @param tags List of tags that the visitor session should contain
         * @return Found visitor session or null
         */
        fun findVisitorSessionByTags(tags: List<String>): VisitorSessionV2? {
            return visitorSessions.find { visitorSession ->
                visitorSession.tags?.any { tags.contains(it) } ?: false
            }
        }
    }

}
