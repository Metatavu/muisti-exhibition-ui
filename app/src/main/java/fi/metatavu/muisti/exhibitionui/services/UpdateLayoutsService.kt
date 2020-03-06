package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.PageLayout
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Service for getting layouts from the API
 */
class UpdateLayoutsService : JobIntentService() {

    private val layoutRepository: LayoutRepository

    /**
     * Constructor
     */
    init {
        val layoutDao = ExhibitionUIDatabase.getDatabase().layoutDao()
        layoutRepository = LayoutRepository(layoutDao)
    }

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId()
            if (exhibitionId != null) {
                val layouts = MuistiApiFactory.getPageLayoutsApi().listPageLayouts()
                addLayouts(layouts)
            }
        }
    }

    /**
     * Adds a list of layouts to Database
     *
     * @param layouts an array of layouts to add to the database
     * @return a visitor session for a task
     */
    private suspend fun addLayouts(layouts: Array<PageLayout>) {
        layoutRepository.setLayouts(layouts)
    }
}