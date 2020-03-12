package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
        val parent = parents.last()
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
    private fun setProperty(parent: View, imageView: ImageView, resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        Log.d(javaClass.name, "Setting property ${property.name} to ${property.value}")

        try {
            when (property.name) {
                "layout_width" -> setImageLayoutWidths(parent, imageView, property)
                "layout_height" -> setImageLayoutHeights(parent, imageView, property)
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
                "gravity" -> setLayoutGravity(parent, imageView, property.value)
                "layout_gravity" -> setLayoutGravity(parent, imageView, property.value)
                "layout_marginTop" -> setLayoutMargin(parent, imageView, property)
                "layout_marginBottom" -> setLayoutMargin(parent, imageView, property)
                "layout_marginRight" -> setLayoutMargin(parent, imageView, property)
                "layout_marginLeft" -> setLayoutMargin(parent, imageView, property)

                else -> Log.d(javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(ImageViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Updates component layout gravity value
     *
     * @param parent parent component
     * @param view view component
     * @param value value
     */
    private fun setLayoutGravityLocal(parent: View, view: View, value: String?) {
        val layoutParams = view.layoutParams
        if (parent is FrameLayout) {
            layoutParams as FrameLayout.LayoutParams
            layoutParams.gravity = parseGravity(value)
        } else if (parent is LinearLayout) {
            layoutParams as LinearLayout.LayoutParams
            layoutParams.gravity = parseGravity(value)
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for gravity"
            )
        }
        view.layoutParams = layoutParams
    }

    /**
     * Sets layout heights
     *
     * @param layout any layout
     * @param property height property to be set
     */
    private fun setImageLayoutHeights(parent: View, layout: View, property: PageLayoutViewProperty) {
        if (parent is FrameLayout) {
            val layoutParams = layout.layoutParams
            var newHeight: Int = layoutParams.height
            when (property.type) {
                PageLayoutViewPropertyType.number -> newHeight = property.value.toInt()
                PageLayoutViewPropertyType.string -> newHeight = when (property.value) {
                    "match_parent" -> FrameLayout.LayoutParams.MATCH_PARENT
                    "wrap_content" -> FrameLayout.LayoutParams.WRAP_CONTENT
                    else -> {
                        getDps(property.value)
                    }
                }
            }
            val updatedParams = FrameLayout.LayoutParams(layoutParams.width, newHeight)
            layout.layoutParams = updatedParams
        } else if (parent is LinearLayout) {
            val layoutParams = layout.layoutParams
            var newHeight: Int = layoutParams.height
            when (property.type) {
                PageLayoutViewPropertyType.number -> newHeight = property.value.toInt()
                PageLayoutViewPropertyType.string -> newHeight = when (property.value) {
                    "match_parent" -> LinearLayout.LayoutParams.MATCH_PARENT
                    "wrap_content" -> LinearLayout.LayoutParams.WRAP_CONTENT
                    else -> {
                        getDps(property.value)
                    }
                }
            }
            val updatedParams = LinearLayout.LayoutParams(layoutParams.width, newHeight)
            layout.layoutParams = updatedParams
        } else if (parent is RelativeLayout) {
            val layoutParams = layout.layoutParams
            var newHeight: Int = layoutParams.height
            when (property.type) {
                PageLayoutViewPropertyType.number -> newHeight = property.value.toInt()
                PageLayoutViewPropertyType.string -> newHeight = when (property.value) {
                    "match_parent" -> RelativeLayout.LayoutParams.MATCH_PARENT
                    "wrap_content" -> RelativeLayout.LayoutParams.WRAP_CONTENT
                    else -> {
                        getDps(property.value)
                    }
                }
            }
            val updatedParams = RelativeLayout.LayoutParams(layoutParams.width, newHeight)
            layout.layoutParams = updatedParams
        }else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for layout_height")
        }
    }

    /**
     * Sets layout widths
     *
     * @param layout any layout
     * @param property width property to be set
     */
    private fun setImageLayoutWidths(parent: View, layout: View, property: PageLayoutViewProperty) {
        if (parent is FrameLayout) {
            val layoutParams = layout.layoutParams
            var newWidth: Int = layoutParams.width
            when (property.type) {
                PageLayoutViewPropertyType.number -> newWidth = property.value.toInt()
                PageLayoutViewPropertyType.string -> newWidth = when (property.value) {
                    "match_parent" -> FrameLayout.LayoutParams.MATCH_PARENT
                    "wrap_content" -> FrameLayout.LayoutParams.WRAP_CONTENT
                    else -> {
                        getDps(property.value)
                    }
                }
            }
            val updatedParams = FrameLayout.LayoutParams(newWidth, layoutParams.height)
            layout.layoutParams = updatedParams
        } else if (parent is LinearLayout) {
            val layoutParams = layout.layoutParams
            var newWidth: Int = layoutParams.width
            when (property.type) {
                PageLayoutViewPropertyType.number -> newWidth = property.value.toInt()
                PageLayoutViewPropertyType.string -> newWidth = when (property.value) {
                    "match_parent" -> LinearLayout.LayoutParams.MATCH_PARENT
                    "wrap_content" -> LinearLayout.LayoutParams.WRAP_CONTENT
                    else -> {
                        getDps(property.value)
                    }
                }
            }
            val updatedParams = LinearLayout.LayoutParams(newWidth, layoutParams.height)
            layout.layoutParams = updatedParams
        } else if (parent is RelativeLayout) {
            val layoutParams = layout.layoutParams
            var newWidth: Int = layoutParams.width
            when (property.type) {
                PageLayoutViewPropertyType.number -> newWidth = property.value.toInt()
                PageLayoutViewPropertyType.string -> newWidth = when (property.value) {
                    "match_parent" -> RelativeLayout.LayoutParams.MATCH_PARENT
                    "wrap_content" -> RelativeLayout.LayoutParams.WRAP_CONTENT
                    else -> {
                        getDps(property.value)
                    }
                }
            }
            val updatedParams = RelativeLayout.LayoutParams(newWidth, layoutParams.height)
            layout.layoutParams = updatedParams
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for layout_width")
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