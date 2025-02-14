package fi.metatavu.muisti.exhibitionui.services

import android.util.Log
import fi.metatavu.muisti.api.client.models.*
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
            Log.d(this.javaClass.name, "Received page update request for page $pageId. Feature is currently not supported")
        }
        val pageCreateListener = MqttTopicListener(super.mqttTopic("/pages/create"), MqttExhibitionPageCreate::class.java) {
            val pageId = it.id ?: return@MqttTopicListener
            Log.d(this.javaClass.name, "Received page create request for page $pageId. Feature is currently not supported")
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
    fun updateAllPages() = GlobalScope.launch {
        try {
            val deviceId = DeviceSettings.getExhibitionDeviceId() ?: return@launch
            val pages = MuistiApiFactory.getDeviceDataApi().listDeviceDataPages(deviceId = deviceId)
            setPages(pages)
        } catch (e: Exception) {
            Log.e(javaClass.name, "Updating all pages failed", e)
        }
    }

    /**
     * Removes a page from the local database
     *
     * @param pageId  id of page to be removed
     */
    private fun removePage(pageId: UUID) {
        GlobalScope.launch {
            try {
                removePages(pageId)
            } catch (e: Exception) {
                Log.e(javaClass.name, "Failed to remove single page", e)
            }
        }
    }

    /**
     * Sets an array of pages into the database and removes all other pages
     *
     * @param pages an array of pages to insert into the database if page with same id exists it will be updated
     */
    private suspend fun setPages(pages: Array<DevicePage>) {
        pageRepository.setPages(
            pages = pages
        )
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