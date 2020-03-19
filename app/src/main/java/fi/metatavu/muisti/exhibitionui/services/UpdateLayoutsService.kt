package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.PageLayout
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.lang.Exception

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
        try {
            GlobalScope.launch {
                val layouts = MuistiApiFactory.getPageLayoutsApi().listPageLayouts()
                addLayouts(layouts)
                Log.d(UpdatePagesService::javaClass.name, "Updated ${layouts.size} layouts.")
            }
        } catch (e: Exception) {
            Log.d(javaClass.name, "Failed to update layouts", e)
        }
    }

    private fun getUpdatedLayout(layoutId: UUID) {
        GlobalScope.launch {
            val layout = MuistiApiFactory.getPageLayoutsApi().findPageLayout(layoutId)
            addLayouts(arrayOf(layout))
        }
    }

    /**
     * Adds a list of layouts to Database
     *
     * @param layouts an array of layouts to add to the database
     * @return a visitor session for a task
     */
    private suspend fun addLayouts(layouts: Array<PageLayout>) {
        layoutRepository.updateLayouts(layouts)
    }
}