package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.VisitorSessionV2
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.visitors.ExhibitionVisitorsContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.OffsetDateTime

/**
 * Service for caching visitor sessions.
 */
class VisitorSessionsService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        updateVisitorSessions()
    }

    /**
     * Updates the visitor sessions list from the API
     */
    private fun updateVisitorSessions() = GlobalScope.launch {
        try {
            Log.d(javaClass.name, "Updating visitor session list...")

            val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
            val newVisitorSessions = MuistiApiFactory.getVisitorSessionsApi().listVisitorSessionsV2(
                exhibitionId = exhibitionId,
                tagId = null,
                modifiedAfter = VISITOR_LIST_MODIFIED_AFTER?.toString()
            )

            val expiredCount = ExhibitionVisitorsContainer.removeExpiredVisitorSessions()
            val visitorSessions = ExhibitionVisitorsContainer.addVisitorSessions(newVisitorSessions)
            val lastModified = visitorSessions
                .sortedBy(VisitorSessionV2::modifiedAt)
                .lastOrNull()
                ?.modifiedAt

            VISITOR_LIST_MODIFIED_AFTER = if (lastModified != null) OffsetDateTime.parse(lastModified) else null

            Log.d(javaClass.name, "Found ${newVisitorSessions.size} new and removed $expiredCount expired visitor sessions. Active visitor session count: ${visitorSessions.size}")
        } catch (e: Exception) {
            Log.e(javaClass.name, "Error updating Exhibition visitor session list: $e")
        }
    }

    companion object {
        var VISITOR_LIST_MODIFIED_AFTER: OffsetDateTime? = null
    }

}