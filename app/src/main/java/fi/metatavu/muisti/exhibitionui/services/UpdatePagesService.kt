package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.ExhibitionPage
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Service for getting pages from the API
 */
class UpdatePagesService : JobIntentService() {

    private val pageRepository: PageRepository

    /**
     * Constructor
     */
    init {
        val pageDao = ExhibitionUIDatabase.getDatabase().pageDao()
        pageRepository = PageRepository(pageDao)
    }

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            Log.d(javaClass.name, "Getting pages from API")
            var exhibitions = MuistiApiFactory.getExhibitionsApi().listExhibitions()
            Log.d(javaClass.name, "Found exhibition:" + exhibitions[0].name)
            val exhibitionId = exhibitions[0].id
            if (exhibitionId != null) {
                val pages = MuistiApiFactory.getExhibitionPagesApi().listExhibitionPages(exhibitionId)
                Log.d(javaClass.name, "Found ${pages.size} pages")
                addPages(pages)
            }
        }
    }

    /**
     * Adds a list of pages to Database
     *
     * @param pages an array of pages to add to the database
     * @return a visitor session for a task
     */
    private suspend fun addPages(pages: Array<ExhibitionPage>) {
        pageRepository.setPages(pages)
    }
}