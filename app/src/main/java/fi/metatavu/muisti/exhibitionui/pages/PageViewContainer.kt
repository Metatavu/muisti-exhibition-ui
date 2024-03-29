package fi.metatavu.muisti.exhibitionui.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * Container for generated page views
 */
class PageViewContainer {

    companion object {

        private val pageViews = mutableMapOf<UUID, PageView>()
        private var livePages = MutableLiveData<List<PageView>>()

        /**
         * Returns generated page view by id
         *
         * @param id view id
         * @return constructed view or null if not found
         */
        fun getPageView(id: UUID): PageView? {
            return pageViews[id]
        }

        fun clear(){
            pageViews.clear()
            livePages = MutableLiveData()
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

        /**
         * Checks if page with id is contained in container
         *
         * @param id Page id to check
         * @return whether or not to page is found in pageViews
         */
        fun contains(id: UUID): Boolean {
            return pageViews.containsKey(id)
        }
    }

}