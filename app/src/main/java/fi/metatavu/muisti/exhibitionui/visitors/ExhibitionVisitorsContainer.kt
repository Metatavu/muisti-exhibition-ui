package fi.metatavu.muisti.exhibitionui.visitors

import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSession
import java.util.*

/**
 * Container for currently visible visitors
 */
class ExhibitionVisitorsContainer {

    companion object {

        private var visitors: Array<Visitor> = arrayOf()

        private var visitorSessions: Array<VisitorSession> = arrayOf()

        /**
         * Sets visitors
         *
         * @param updatedVisitorsList visitors to set
         */
        fun setVisitors(updatedVisitorsList: Array<Visitor>) {
            visitors = updatedVisitorsList
        }

        /**
         * Sets visitor sessions
         *
         * @param updatedVisitorSessions visitor sessions to set
         */
        fun setVisitorSessions(updatedVisitorSessions: Array<VisitorSession>) {
            visitorSessions = updatedVisitorSessions
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
         * Finds a visitor by Id
         *
         * @param visitorId Visitor id to find visitor with
         * @return Visitor or null
         */
        fun findVisitorById(visitorId: UUID): Visitor? {
            return visitors.firstOrNull{ it.id == visitorId }
        }

        /**
         * Finds a visitor session by the specified tag
         *
         * @param tag Tag to find visitor session with
         * @return Visitor Session or null
         */
        fun findVisitorSessionByTag(tag: String): VisitorSession? {
            val matchingVisitor = visitors.firstOrNull {
                it.tagId == tag
            } ?: return null

            return visitorSessions.firstOrNull {
                it.visitorIds.contains(matchingVisitor.id)
            }
        }

        /**
         * Returns a Visitor session that contains all the specified tags
         *
         * @param tags List of tags that the visitor session should contain
         * @return Found visitor session or null
         */
        fun findVisitorSessionByTags(tags: List<String>): VisitorSession? {
            val matchingVisitors = visitors.filter {
                tags.contains(it.tagId)
            }

            return visitorSessions.firstOrNull { visitorsession ->
                matchingVisitors.all { visitorsession.visitorIds.contains(it.id) }
            }
        }
    }

}