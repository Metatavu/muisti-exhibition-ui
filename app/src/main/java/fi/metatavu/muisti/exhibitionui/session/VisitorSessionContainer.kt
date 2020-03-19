package fi.metatavu.muisti.exhibitionui.session

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

        private var visitorSessionId: UUID? = null

        /**
         * Returns current visitor session id
         *
         * @return current visitor session id
         */
        fun getVisitorSessionId(): UUID? {
            return visitorSessionId
        }

        /**
         * Sets current visitor session id
         *
         * @param visitorSessionId visitor session id
         */
        fun setVisitorSessionId(visitorSessionId: UUID?) {
            this.visitorSessionId = visitorSessionId
        }
    }

}