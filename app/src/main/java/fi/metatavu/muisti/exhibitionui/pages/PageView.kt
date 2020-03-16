package fi.metatavu.muisti.exhibitionui.pages

import android.view.View
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Type of page view activator callback method.
 */
typealias PageViewActivator = (pageActivity: PageActivity) -> Unit

/**
 * Data class for storing generated page views
 *
 * @property view view
 * @property page page
 * @property activators page view activators
 */
data class PageView (

    val view: View,

    val page: Page,

    val activators: MutableList<PageViewActivator>
) {

    override fun toString(): String {
        return page.name
    }

}