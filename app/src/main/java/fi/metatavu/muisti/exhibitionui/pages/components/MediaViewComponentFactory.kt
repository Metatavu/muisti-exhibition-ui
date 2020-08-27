package fi.metatavu.muisti.exhibitionui.pages.components

import android.view.View
import fi.metatavu.muisti.api.client.models.ExhibitionPageResourceType

/**
 * Component factory for media view components
 *
 * MediaView component chooses appropriate display component using type of given resource
 */
class MediaViewComponentFactory : AbstractComponentFactory<View>() {
    override val name: String
        get() = "MediaView"

    override fun buildComponent(buildContext: ComponentBuildContext): View {
        val srcProperty = buildContext.pageLayoutView.properties.firstOrNull { it.name == "src" }
        val srcResource = getResource(buildContext.page.resources, srcProperty?.value)

        when (srcResource?.type) {
            ExhibitionPageResourceType.video -> return PlayerViewComponentFactory().buildComponent(buildContext)
            ExhibitionPageResourceType.html -> return WebViewComponentFactory().buildComponent(buildContext)
            else -> return ImageViewComponentFactory().buildComponent(buildContext)
        }
    }

}