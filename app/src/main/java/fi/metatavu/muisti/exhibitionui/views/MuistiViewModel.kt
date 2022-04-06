package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import fi.metatavu.muisti.api.client.models.VisitorSessionV2
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import java.util.*

/**
 * View model for main activity
 *
 * @constructor
 * model constructor
 *
 * @param application application instance
 */
class MuistiViewModel(application: Application): AndroidViewModel(application) {

    private val pageRepository: PageRepository = PageRepository(ExhibitionUIDatabase.getDatabase().pageDao())

    /**
     * Resolves a id of index page for given language
     *
     * @param language language
     * @param visitorSession visitor session
     * @return a front page id for given language or null if not found
     */
    suspend fun getIndexPage(
        language: String,
        visitorSession: VisitorSessionV2
    ) : UUID? {
        val indexPages = pageRepository.listIndexPages(
            language = language
        )

        val activeIndexPages = indexPages
            .sortedWith(nullsLast(compareBy(Page::activeConditionUserVariable)))
            .filter { indexPage ->
                val activeConditionUserVariable = indexPage.activeConditionUserVariable
                if (!activeConditionUserVariable.isNullOrEmpty()) {
                    val variable = visitorSession.variables?.find { variable -> variable.name == activeConditionUserVariable }
                    variable?.value == indexPage.activeConditionEquals
                } else {
                    true
                }
            }

        if (activeIndexPages.size > 1) {
            Log.w(javaClass.name, "Several matching index pages found, returning first one")
        } else if (activeIndexPages.isEmpty()) {
            Log.w(javaClass.name, "Could not find index page for visitor session ${visitorSession.id}")
        }

        return activeIndexPages.firstOrNull()?.pageId
    }

    /**
     * Returns idle page id for the current device
     *
     * @return a idle page id for the current device or null if not found
     */
    suspend fun getIdlePageId() : UUID? {
        val deviceId = DeviceSettings.getExhibitionDeviceId()
        val exhibitionId = DeviceSettings.getExhibitionId()
        val device = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId ?: return null, deviceId ?: return null)

        return device.idlePageId
    }
}