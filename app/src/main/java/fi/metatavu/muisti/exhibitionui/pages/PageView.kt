package fi.metatavu.muisti.exhibitionui.pages

import android.view.View
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Interface that describes a page view life-cycle listener
 *
 */
interface PageViewLifecycleListener {

    /**
     * Method invoked when page is being activated
     *
     * @param pageActivity page activity instance
     */
    fun onPageActivate (pageActivity: PageActivity)


    /**
     * Method invoked when page is being deactivated
     *
     * @param pageActivity page activity instance
     */
    fun onPageDeactivate (pageActivity: PageActivity)

}

/**
 * Data class for storing generated page views
 *
 * @property view view
 * @property page page
 * @property lifecycleListeners life-cycle listeners
 */
data class PageView (

    val view: View,

    val page: Page,

    val lifecycleListeners: List<PageViewLifecycleListener>
) {

    override fun toString(): String {
        return page.name
    }

}