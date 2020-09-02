package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.dao.PageDao
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import java.lang.Exception
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

    private val pageRepository: PageRepository = PageRepository(ExhibitionUIDatabase.getDatabase().pageDao())

    /**
     * Logs the visitor in
     *
     * @param exhibitionId exhibition id
     * @param tagId visitor tag id
     */
    suspend fun visitorLogin(exhibitionId: UUID, tagId: String) {
        /**
        val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()

        val visitorSessions = visitorSessionsApi.listVisitorSessions(exhibitionId)
        // TODO: Add better support for finding visitor session by tag
        var visitorSession = visitorSessions.firstOrNull{ it.visitorIds.firstOrNull{ it.tagId == tagId } != null }

        if (visitorSession == null) {
            val users: Array<VisitorSessionUser> = arrayOf(VisitorSessionUser(userId = UUID.randomUUID(), tagId = tagId))
            visitorSession = visitorSessionsApi.createVisitorSession(exhibitionId, VisitorSession(
                VisitorSessionState.aCTIVE, users, null, exhibitionId)
            )
        }

        VisitorSessionContainer.setVisitorSessionId(visitorSession.id)
        **/
    }

    /**
     * Get front page for a device
     * @param exhibitionId Exhibition the device belongs to
     * @param deviceId Id of the device
     *
     * @return Id of the page that has been set as the front page for the device
     */
    suspend fun getFrontPage(exhibitionId: UUID, deviceId: UUID) : UUID? {
        // TODO: Resolve locale
        return pageRepository.findIndexPage(locale = "fi")?.pageId

        /**
        return try {
            MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = deviceId).indexPageId
        } catch (e: Exception) {
            Log.e(javaClass.name, "Failed to get front page or device: $deviceId")
            null
        }
        return null
         **/
    }
}