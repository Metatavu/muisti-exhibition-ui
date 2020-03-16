package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.view.View
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.ExhibitionPageResourceType
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.pages.PageViewActivator

/**
 * Component factory for media view components
 *
 * MediaView component chooses appropriate display component using type of given resource
 */
class MediaViewComponentFactory : AbstractComponentFactory<View>() {
    override val name: String
        get() = "MediaView"

    override fun buildComponent(context: Context, parents: Array<View>, pageLayoutView: PageLayoutView, resources: Array<ExhibitionPageResource>, activators: MutableList<PageViewActivator>): View {
        val srcProperty = pageLayoutView.properties.firstOrNull { it.name == "src" }
        val srcResource = getResource(resources, srcProperty?.value)

        if (srcResource?.type == ExhibitionPageResourceType.video) {
            return PlayerViewComponentFactory().buildComponent(context, parents, pageLayoutView, resources, activators)
        } else {
            return ImageViewComponentFactory().buildComponent(context, parents, pageLayoutView, resources, activators)
        }

    }

}