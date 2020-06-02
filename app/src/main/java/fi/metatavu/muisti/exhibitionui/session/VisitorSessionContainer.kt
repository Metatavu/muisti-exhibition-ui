package fi.metatavu.muisti.exhibitionui.session

import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.api.client.models.VisitorSessionUser
import fi.metatavu.muisti.api.client.models.VisitorSessionState
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

/**
 * Container for visitor session
 */
class VisitorSessionContainer {

    companion object {

        private var visitorSession: VisitorSession? = null

        private val currentVisitors: MutableList<Visitor> = mutableListOf()

        fun getCurrentVisitors() : List<Visitor>{
            return currentVisitors
        }

        fun addCurrentVisitor(visitor: Visitor) {
            currentVisitors.add(visitor)
        }

        fun removeCurrentVisitor(visitor: Visitor) {
            currentVisitors.remove(visitor)
        }

        fun clearCurrentVisitors(visitor: Visitor) {
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
         * Sets current visitor session id
         *
         * @param visitorSessionId visitor session id
         */
        fun setVisitorSession(visitorSession: VisitorSession?) {
            this.visitorSession = visitorSession
        }
    }

}