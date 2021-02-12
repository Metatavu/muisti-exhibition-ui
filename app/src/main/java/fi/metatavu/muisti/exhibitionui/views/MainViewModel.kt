package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
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
class MainViewModel(application: Application): AndroidViewModel(application) {

    private val pageRepository: PageRepository = PageRepository(ExhibitionUIDatabase.getDatabase().pageDao())

    /**
     * Resolves a id of front page for given language
     *
     * @param language language
     * @return a front page id for given language or null if not found
     */
    suspend fun getFrontPage(language: String) : UUID? {
        return pageRepository.findIndexPage(language = language)?.pageId
    }

    /**
     * Returns idle page id for the current device
     *
     * @return a idle page id for the current device or null if not found
     */
    suspend fun getIdlePageId() : UUID? {
        try {
            val deviceId = DeviceSettings.getExhibitionDeviceId()
            val exhibitionId = DeviceSettings.getExhibitionId()
            val device = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId ?: return null, deviceId ?: return null)
            return device.idlePageId
        } catch (e: Exception) {
            Log.d(javaClass.name, "Could not get idle page: ${e.message}")
            return null
        }
    }
}
