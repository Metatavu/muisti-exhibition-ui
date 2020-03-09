package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.view.View
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

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
     * @param properties array of view properties
     * @return build view
     */
    fun buildComponent(context: Context, parents: Array<View>, properties : Array<PageLayoutViewProperty>) : T

}