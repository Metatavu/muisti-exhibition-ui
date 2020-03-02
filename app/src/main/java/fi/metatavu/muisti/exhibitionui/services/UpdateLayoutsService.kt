package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayout
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.api.client.models.VisitorSessionVariable
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask as UpdateUserValueTask1

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
            val allLayouts = layoutRepository.listAll()

            allLayouts?.forEach {
                Log.d(javaClass.name, it.name)
            }

            val exhibitionId = DeviceSettings.getExhibitionId()
            if(exhibitionId != null){
                val layouts = MuistiApiFactory.exhibitionPageLayoutsApi().listExhibitionPageLayouts(exhibitionId)
                addLayouts(layouts)
            }
        }
    }

    /**
     * Adds a list of layouts to Database
     *
     * @param updateUserValueTask task
     * @return a visitor session for a task
     */
    private suspend fun addLayouts(layouts: Array<ExhibitionPageLayout>) {
        layoutRepository.setLayouts(layouts)
    }
}