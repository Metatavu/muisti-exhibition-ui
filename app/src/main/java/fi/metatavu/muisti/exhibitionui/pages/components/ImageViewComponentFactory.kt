package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType

/**
 * Component factory for image components
 */
class ImageViewComponentFactory : AbstractComponentFactory<ImageView>() {
    override val name: String
        get() = "ImageView"

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            this.setProperty(imageView, resources, it)
        }

        return imageView
    }

    /**
     * Sets view image property
     *
     * @param imageView image view component
     * @param property property
     */
    private fun setProperty(imageView: ImageView, resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "layout_width" -> when (property.type) {
                    PageLayoutViewPropertyType.number -> imageView.layoutParams.width =
                        property.value.toInt()
                    PageLayoutViewPropertyType.string -> when (property.value) {
                        "match_parent" -> imageView.layoutParams.width =
                            ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> imageView.layoutParams.width =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "layout_height" -> when (property.type) {
                    PageLayoutViewPropertyType.number -> imageView.layoutParams.height =
                        property.value.toInt()
                    PageLayoutViewPropertyType.string -> when (property.value) {
                        "match_parent" -> imageView.layoutParams.height =
                            ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> imageView.layoutParams.height =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
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
                "tag" -> imageView.tag = property.value
                else -> Log.d(javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(ImageViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets a image src
     *
     * @param imageView image view component
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