package fi.metatavu.muisti.exhibitionui.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * Container for generated page views
 */
class PageViewContainer {

    companion object {

        private val pageViews= mutableMapOf<UUID, PageView>()
        private val livePages = MutableLiveData<List<PageView>>()

        /**
         * Returns generated page view by id
         *
         * @param id view id
         * @return constructed view or null if not found
         */
        fun getPageView(id: UUID): PageView? {
            return pageViews[id]
        }

        /**
         * Stores generated page view
         *
         * @param id view id
         * @param pageView constructed view
         */
        fun setPageView(id: UUID, pageView: PageView) {
            pageViews[id] = pageView
            livePages.postValue(pageViews.values.toList())
        }

        /**
         * Returns a LiveData instance for pages view list
         *
         * @return a LiveData instance for pages view list
         */
        fun livePageViewList(): LiveData<List<PageView>>  {
            return livePages
        }
    }

}