package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
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
}