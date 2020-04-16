package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.ExhibitionPage
import fi.metatavu.muisti.api.client.models.MqttExhibitionPageCreate
import fi.metatavu.muisti.api.client.models.MqttExhibitionPageDelete
import fi.metatavu.muisti.api.client.models.MqttExhibitionPageUpdate
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttActionInterface
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

/**
 * Service for getting pages from the API
 */
class UpdatePagesService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        try {
            UpdatePages.updateAllPages()
        } catch (e: Exception) {
            Log.d(javaClass.name, "Pages update failed", e)
        }
    }
}

object UpdatePages : MqttActionInterface {

    private val pageRepository: PageRepository

    /**
     * Constructor
     */
    init {
        val pageDao = ExhibitionUIDatabase.getDatabase().pageDao()
        pageRepository = PageRepository(pageDao)
    }

    override fun getMqttTopicListeners(): List<MqttTopicListener<*>> {
        val pageUpdateListener = MqttTopicListener(super.mqttTopic("/pages/update"), MqttExhibitionPageUpdate::class.java) {
            val pageId = it.id ?: return@MqttTopicListener
            updateSinglePage(pageId)
        }
        val pageCreateListener = MqttTopicListener(super.mqttTopic("/pages/create"), MqttExhibitionPageCreate::class.java) {
            val pageId = it.id ?: return@MqttTopicListener
            updateSinglePage(pageId)
        }
        val pageDeleteListener = MqttTopicListener(super.mqttTopic("pages/delete"), MqttExhibitionPageDelete::class.java) {
            val pageId = it.id ?: return@MqttTopicListener
            removePage(pageId)
        }
        return listOf(pageUpdateListener, pageCreateListener, pageUpdateListener, pageDeleteListener)
    }

    /**
     * Retrieves all pages from from the currently selected exhibition and saves them to the local database
     */
    fun updateAllPages() {
        GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId()
            if (exhibitionId != null) {
                val pages =
                    MuistiApiFactory.getExhibitionPagesApi().listExhibitionPages(
                        exhibitionId = exhibitionId,
                        exhibitionContentVersionId = null,
                        exhibitionDeviceId = null
                    )
                addPages(pages)
            }
        }
    }

    /**
     * Updates a single page from from the currently selected exhibition and saves it to the local database
     *
     * @param pageId pageId to delete
     */
    private fun updateSinglePage(pageId: UUID) {
        GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId()
            if (exhibitionId != null) {
                val pages =
                    MuistiApiFactory.getExhibitionPagesApi().findExhibitionPage(exhibitionId, pageId)
                addPages(arrayOf(pages))
            }
        }
    }

    /**
     * Removes a page from the local database
     *
     * @param pageId  id of page to be removed
     */
    private fun removePage(pageId: UUID) {
        GlobalScope.launch {
            removePages(pageId)
        }
    }

    /**
     * Adds a list of pages to Database
     *
     * @param pages an array of pages to add to the database
     */
    private suspend fun addPages(pages: Array<ExhibitionPage>) {
        pageRepository.setPages(pages)
    }

    /**
     * Removes a page from the database
     *
     * @param pageId id of page to be removed
     */
    private suspend fun removePages(pageId: UUID) {
        pageRepository.deletePage(pageId)
    }
}