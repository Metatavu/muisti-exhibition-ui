package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.view.View
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.pages.PageViewVisitorSessionListener
import fi.metatavu.muisti.exhibitionui.persistence.model.Page

/**
 * Component build context class
 *
 * @property context context
 * @property parents parent components
 * @property pageLayoutView page layout view
 * @property page page
 * @property lifecycleListeners list of lifecycle listeners
 * @property visitorSessionListeners list of visitor session listeners
 */
class ComponentBuildContext (val context: Context, val parents: Array<View>, val pageLayoutView: PageLayoutView, val page: Page, val lifecycleListeners: MutableList<PageViewLifecycleListener>, val visitorSessionListeners: MutableList<PageViewVisitorSessionListener>) {

    /**
     * Registers new life-cycle listener
     *
     * @param listener listener
     */
    fun addLifecycleListener(listener: PageViewLifecycleListener) {
        lifecycleListeners.add(listener)
    }

    /**
     * Registers new visitor session listener
     *
     * @param listener listener
     */
    fun addVisitorSessionListener(listener: PageViewVisitorSessionListener) {
        visitorSessionListeners.add(listener)
    }

}