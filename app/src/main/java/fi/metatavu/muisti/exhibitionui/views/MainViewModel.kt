package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.api.client.models.VisitorSessionState
import fi.metatavu.muisti.api.client.models.VisitorSessionUser
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import java.util.*

/**
 * View model for main activity
 *
 * @constructor
 * model constructor
 *
 * @param application application instance
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Logs the visitor in
     *
     * @param exhibitionId exhibition id
     * @param tagId visitor tag id
     */
    suspend fun visitorLogin(exhibitionId: UUID, tagId: String) {
        val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()

        val visitorSessions = visitorSessionsApi.listVisitorSessions(exhibitionId)
        // TODO: Add better support for finding visitor session by tag
        var visitorSession = visitorSessions.firstOrNull{ it.users.firstOrNull{ it.tagId == tagId } != null }

        if (visitorSession == null) {
            val users: Array<VisitorSessionUser> = arrayOf(VisitorSessionUser(userId = UUID.randomUUID(), tagId = tagId))
            visitorSession = visitorSessionsApi.createVisitorSession(exhibitionId, VisitorSession(
                VisitorSessionState.aCTIVE, users, null, exhibitionId)
            )
        }

        VisitorSessionContainer.setVisitorSessionId(visitorSession.id)
    }

    suspend fun getFrontPage(exhibitionId: UUID, deviceId: UUID) : UUID?{
        return MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = deviceId).indexPageId
    }
}