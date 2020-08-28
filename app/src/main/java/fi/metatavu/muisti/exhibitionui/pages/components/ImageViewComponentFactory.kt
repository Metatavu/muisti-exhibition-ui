package fi.metatavu.muisti.exhibitionui.pages.components

import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for image components
 */
class ImageViewComponentFactory : AbstractComponentFactory<ImageView>() {
    override val name: String
        get() = "ImageView"

    override fun buildComponent(buildContext: ComponentBuildContext): ImageView {
        val imageView = ImageView(buildContext.context)
        setupView(buildContext, imageView)

        val parent = buildContext.parents.lastOrNull()
        imageView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, imageView, it)
        }

        return imageView
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: ImageView, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "src" -> setSrc(buildContext, view, property.value)
                else -> super.setProperty(buildContext, parent, view, property)
            }


        } catch (e: Exception) {
            Log.d(ImageViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets a image src
     *
     * @param buildContext build context
     * @param imageView image view component
     * @param value value
     */
    private fun setSrc(buildContext: ComponentBuildContext, imageView: ImageView, value: String?) {
        val resource = getResourceData(buildContext, value)
        val url = getUrl(resource ?: value)
        url ?: return
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        imageView.setImageBitmap(bmp)
    }

}