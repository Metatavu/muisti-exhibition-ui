package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for image components
 */
class ImageViewComponentFactory : AbstractComponentFactory<ImageView>() {
    override val name: String
        get() = "ImageView"

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): ImageView {
        val imageView = ImageView(context)
        setId(imageView, id)

        val parent = parents.lastOrNull()
        imageView.layoutParams = getInitialLayoutParams(parent)

        properties.forEach {
            this.setProperty(parent, imageView, resources, it)
        }

        return imageView
    }

    /**
     * Sets view image property
     * @param parent parent component
     * @param imageView image view component
     * @param resources resources
     * @param property property
     */
    private fun setProperty(parent: View?, imageView: ImageView, resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "layout_width" -> setLayoutWidth(parent, imageView, property)
                "layout_height" -> setLayoutHeight(parent, imageView, property)
                "width" -> imageView.layoutParams.width = property.value.toInt()
                "height" -> imageView.layoutParams.height = property.value.toInt()
                "background" -> imageView.setBackgroundColor(Color.parseColor(property.value))
                "paddingLeft" -> imageView.setPadding(
                    property.value.toInt(),
                    imageView.paddingTop,
                    imageView.paddingRight,
                    imageView.paddingBottom
                )
                "paddingTop" -> imageView.setPadding(
                    imageView.paddingLeft,
                    property.value.toInt(),
                    imageView.paddingRight,
                    imageView.paddingBottom
                )
                "paddingRight" -> imageView.setPadding(
                    imageView.paddingLeft,
                    imageView.paddingTop,
                    property.value.toInt(),
                    imageView.paddingBottom
                )
                "paddingBottom" -> imageView.setPadding(
                    imageView.paddingLeft,
                    imageView.paddingTop,
                    imageView.paddingRight,
                    property.value.toInt()
                )
                "src" -> setSrc(imageView, resources, property.value)
                "layout_gravity" -> setLayoutGravity(imageView, property.value)
                "layout_marginTop" -> setLayoutMargin(parent, imageView, property)
                "layout_marginBottom" -> setLayoutMargin(parent, imageView, property)
                "layout_marginRight" -> setLayoutMargin(parent, imageView, property)
                "layout_marginLeft" -> setLayoutMargin(parent, imageView, property)
                "layout_toRightOf" -> setLayoutOf(imageView, property)

                else -> Log.d(ImageViewComponentFactory::javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(ImageViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets a image src
     *
     * @param imageView image view component
     * @param resources resources
     * @param value value
     */
    private fun setSrc(imageView: ImageView, resources: Array<ExhibitionPageResource>, value: String?) {
        val resource = getResource(resources, value)
        val url = getUrl(resource ?: value)
        url ?: return
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        imageView.setImageBitmap(bmp)
    }

}