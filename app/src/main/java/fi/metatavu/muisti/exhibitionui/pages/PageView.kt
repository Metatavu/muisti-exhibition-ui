package fi.metatavu.muisti.exhibitionui.pages

import android.os.Bundle
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

    /**
     * Method invoked when page is on low memory
     *
     */
    fun onLowMemory ()

    /**
     * Method invoked when page is resumed
     *
     */
    fun onResume ()

    /**
     * Method invoked when page is paused
     *
     */
    fun onPause ()

    /**
     * Method invoked when page is stopped
     *
     */
    fun onStop ()

    /**
     * Method invoked when instance state is saved
     *
     * @param outState state
     */
    fun onSaveInstanceState (outState: Bundle)

    /**
     * Method invoked when page is destroyed
     */
    fun onDestroy ()
}

/**
 * Data class for storing generated page views
 *
 * @property orientation page orientation
 * @property view view
 * @property page page
 * @property lifecycleListeners life-cycle listeners
 */
data class PageView (

    val orientation: Int,

    val view: View,

    val page: Page,

    val lifecycleListeners: List<PageViewLifecycleListener>
) {

    override fun toString(): String {
        return page.name
    }

}