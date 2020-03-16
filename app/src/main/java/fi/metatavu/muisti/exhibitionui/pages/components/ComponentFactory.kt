package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.view.View
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.pages.PageViewActivator

/**
 * Interface descrining component factory
 *
 * @param T view
 */
interface ComponentFactory <T : View> {

    /**
     * Component factory name
     */
    val name: String

    /**
     * Builds a view component
     *
     * @param context context
     * @param parents list of parent components
     * @param pageLayoutView page layout view
     * @param resources list of resources
     * @param activators list of activators
     * @return build view
     */
    fun buildComponent(context: Context, parents: Array<View>, pageLayoutView: PageLayoutView, resources: Array<ExhibitionPageResource>, activators: MutableList<PageViewActivator>) : T
}