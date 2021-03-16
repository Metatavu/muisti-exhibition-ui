package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.visitors.ExhibitionVisitorsContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Service for caching Visitors and Visitor sessions.
 */
class VisitorsService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        updateVisitors()
        updateVisitorSessions()
    }

    /**
     * Updates the visitors list from the database
     */
    private fun updateVisitors() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
        val visitorsList = MuistiApiFactory.getVisitorsApi().listVisitors(exhibitionId = exhibitionId, email = null, tagId = null)
        ExhibitionVisitorsContainer.setVisitors(visitorsList)
    }

    /**
     * Updates the visitor sessions list from the database
     */
    private fun updateVisitorSessions() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
        val visitorSessionList = MuistiApiFactory.getVisitorSessionsApi().listVisitorSessions(exhibitionId = exhibitionId, tagId = null)
        ExhibitionVisitorsContainer.setVisitorSessions(visitorSessionList)
    }
}