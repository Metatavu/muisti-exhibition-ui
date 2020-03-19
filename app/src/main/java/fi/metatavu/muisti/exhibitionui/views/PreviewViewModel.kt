package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import fi.metatavu.muisti.exhibitionui.pages.PageView
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer

/**
 * View model for preview activity
 *
 * @constructor
 * model constructor
 *
 * @param application application instance
 */
class PreviewViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Returns a LiveData instance for pages view list
     *
     * @return a LiveData instance for pages view list
     */
    fun livePageViewList(): LiveData<List<PageView>> {
        return PageViewContainer.livePageViewList()
    }

}