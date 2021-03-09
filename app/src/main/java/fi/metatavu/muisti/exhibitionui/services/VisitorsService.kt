package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.apis.VisitorSessionsApi
import fi.metatavu.muisti.api.client.apis.VisitorsApi
import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Service for caching Visitors and Visitor sessions.
 */
class VisitorsService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        updateVisitors()
        updateVisitorSessions()
    }

    companion object {

        private var visitorSessions: Array<VisitorSession> = arrayOf()
        private var visitors: Array<Visitor> = arrayOf()
        private var visitorsApi: VisitorsApi? = null
        private var visitorSessionsApi: VisitorSessionsApi? = null

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

        /**
         * Returns a new visitor api or a stored instance if it has been previously used
         *
         * @return Visitor Api
         */
        private suspend fun getVisitorsApi(): VisitorsApi {
            return visitorsApi ?: MuistiApiFactory.getVisitorsApi().also {
                visitorsApi = it
            }
        }

        /**
         * Returns a new visitor sessions api or a stored instance if it has been previously used
         *
         * @return Visitor Sessions Api
         */
        private suspend fun getVisitorsSessionsApi(): VisitorSessionsApi {
            return visitorSessionsApi ?: MuistiApiFactory.getVisitorSessionsApi().also {
                visitorSessionsApi = it
            }
        }

        /**
         * Updates the visitors list from the database
         */
        private fun updateVisitors() = GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
            val visitorsList = getVisitorsApi().listVisitors(exhibitionId = exhibitionId, email = null, tagId = null)
            visitors = visitorsList
        }

        /**
         * Updates the visitor sessions list from the database
         */
        private fun updateVisitorSessions() = GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
            val visitorSessionList = getVisitorsSessionsApi().listVisitorSessions(exhibitionId = exhibitionId, tagId = null)
            visitorSessions = visitorSessionList
        }
    }
}