package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.visitors.ExhibitionVisitorsContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Service for caching visitors.
 */
class VisitorsService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        updateVisitors()
    }

    /**
     * Updates the visitors list from the API
     */
    private fun updateVisitors() = GlobalScope.launch {
        try {
            val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
            val visitorsList = MuistiApiFactory.getVisitorsApi().listVisitors(exhibitionId = exhibitionId, email = null, tagId = null)
            ExhibitionVisitorsContainer.setVisitors(visitorsList)
        } catch (e: Exception) {
            Log.e(javaClass.name, "Error updating Exhibition visitor list: $e")
        }
    }

}