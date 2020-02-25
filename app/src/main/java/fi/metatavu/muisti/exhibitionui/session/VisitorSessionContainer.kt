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
         * Returns current visitor session
         *
         * @return current visitor session
         */
        suspend fun getVisitorSessionId(): UUID? {
            return GlobalScope.async {
                if (visitorSessionId == null) {
                    val users: Array<VisitorSessionUser> = arrayOf(VisitorSessionUser(UUID.randomUUID(), "faketag"))
                    val exhibitionId = DeviceSettings.getExhibitionId()!!
                    val visitorSession = VisitorSession(users, null, exhibitionId, VisitorSessionState.aCTIVE )
                    val result = MuistiApiFactory.getVisitorSessionsApi().createVisitorSession(exhibitionId, visitorSession)
                    visitorSessionId = result.id!!
                }

                visitorSessionId
            }.await()
        }
    }

}