package fi.metatavu.muisti.exhibitionui.session

import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSession
import java.util.*

/**
 * Container for visitor session
 */
class VisitorSessionContainer {

    companion object {

        private var visitorSession: VisitorSession? = null

        private val currentVisitors: MutableList<Visitor> = mutableListOf()

        /**
         * Get current visitor list
         *
         * @return List of Visitors
         */
        fun getCurrentVisitors() : List<Visitor>{
            return currentVisitors
        }

        /**
         * Add visitor
         *
         * @param visitor to add to current visitors
         * @return List of Visitors
         */
        fun addCurrentVisitor(visitor: Visitor) {
            currentVisitors.add(visitor)
        }

        /**
         * Remove current visitor
         *
         * @param visitor Visitor to remove
         */
        fun removeCurrentVisitor(visitor: Visitor) {
            currentVisitors.remove(visitor)
        }

        /**
         * Clears current visitor list
         */
        fun clearCurrentVisitors() {
            currentVisitors.clear()
        }

        /**
         * Returns current visitor session
         *
         * @return current visitor session
         */
        fun getVisitorSession(): VisitorSession? {
            return visitorSession
        }

        /**
         * Returns current visitor session id
         *
         * @return current visitor session id
         */
        fun getVisitorSessionId(): UUID? {
            return visitorSession?.id
        }

        /**
         * Sets current visitor session
         *
         * @param visitorSession visitor session
         */
        fun setVisitorSession(visitorSession: VisitorSession?) {
            this.visitorSession = visitorSession
        }
    }

}