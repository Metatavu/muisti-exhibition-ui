package fi.metatavu.muisti.exhibitionui.services

import android.util.Log
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttActionInterface
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
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
    fun updateAllPages() = GlobalScope.launch {
        try {
            val exhibitionId = DeviceSettings.getExhibitionId()
            val deviceId = DeviceSettings.getExhibitionDeviceId()

            if (exhibitionId != null && deviceId != null) {
                var pages = MuistiApiFactory.getExhibitionPagesApi().listExhibitionPages(
                    exhibitionId = exhibitionId,
                    contentVersionId = null,
                    pageLayoutId = null,
                    exhibitionDeviceId = deviceId
                )

                val idlePageId = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(
                        exhibitionId = exhibitionId,
                        deviceId = deviceId
                ).idlePageId

                if (idlePageId != null) {
                    val idlePage = MuistiApiFactory.getExhibitionPagesApi().findExhibitionPage(exhibitionId, idlePageId)
                    pages = pages.plus(idlePage)
                }

                val contentVersions = pages
                    .map { page -> page.contentVersionId }
                    .distinct()
                    .map { MuistiApiFactory.getContentVersionsApi().findContentVersion(exhibitionId = exhibitionId, contentVersionId = it )  }
                    .toTypedArray()

                setPages(pages, contentVersions)
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "Updating all pages failed", e)
        }
    }

    /**
     * Updates a single page from from the currently selected exhibition and saves it to the local database
     *
     * @param pageId pageId to delete
     */
    private fun updateSinglePage(pageId: UUID) {
        Log.d(javaClass.name, "Updating page: $pageId")
        GlobalScope.launch {
            try {
                val exhibitionId = DeviceSettings.getExhibitionId()
                if (exhibitionId != null) {
                    val page = MuistiApiFactory.getExhibitionPagesApi().findExhibitionPage(
                        exhibitionId = exhibitionId,
                        pageId = pageId
                    )

                    val contentVersion = MuistiApiFactory.getContentVersionsApi().findContentVersion(
                        exhibitionId = exhibitionId,
                        contentVersionId = page.contentVersionId
                    )

                    val localPage = updatePage(page, contentVersion)
                    if (localPage != null) {
                        ConstructPagesService.constructPage(localPage)
                    }
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, "Single page update failed", e)
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
     * @param contentVersions an array of content versions related to pages
     */
    private suspend fun setPages(pages: Array<ExhibitionPage>, contentVersions: Array<ContentVersion>) {
        pageRepository.setPages(
            pages = pages,
            contentVersions = contentVersions
        )
    }

    /**
     * Updates a page to database
     *
     * @param page a page
     */
    private suspend fun updatePage(page: ExhibitionPage, contentVersion: ContentVersion): Page? {
        return pageRepository.updatePage(
            page = page,
            contentVersion = contentVersion
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