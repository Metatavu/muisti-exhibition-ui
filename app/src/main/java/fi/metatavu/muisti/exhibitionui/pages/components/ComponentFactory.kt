package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.view.View
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener

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
     * @param buildContext component build context
     * @return build view
     */
    fun buildComponent(buildContext: ComponentBuildContext) : T
}