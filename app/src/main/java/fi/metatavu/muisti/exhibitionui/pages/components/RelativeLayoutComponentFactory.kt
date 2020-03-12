package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for relative layout components
 */
class RelativeLayoutComponentFactory : AbstractComponentFactory<RelativeLayout>() {

    override val name: String
        get() = "RelativeLayout"

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): RelativeLayout {
        val frameLayout = RelativeLayout(context)
        frameLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            this.setProperty(frameLayout, it)
        }
        return frameLayout
    }

    /**
     * Sets a property
     *
     * @param frameLayout frame layout
     * @param property property
     */
    private fun setProperty(frameLayout: RelativeLayout, property: PageLayoutViewProperty) {
        Log.d(javaClass.name, "Setting property ${property.name} to ${property.value}")
        when(property.name) {
            "layout_width" -> setLayoutWidths(frameLayout, property)
            "layout_height" -> setLayoutHeights(frameLayout, property)
            "background" -> setBackgroundColor(frameLayout, property.value)
            "paddingLeft" -> frameLayout.setPadding(property.value.toInt(), frameLayout.paddingTop, frameLayout.paddingRight, frameLayout.paddingBottom)
            "paddingTop" -> frameLayout.setPadding(frameLayout.paddingLeft, property.value.toInt(), frameLayout.paddingRight, frameLayout.paddingBottom)
            "paddingRight" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, property.value.toInt(), frameLayout.paddingBottom)
            "paddingBottom" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, frameLayout.paddingRight, property.value.toInt())
            "tag" -> frameLayout.tag = property.value
            "layout_gravity" -> frameLayout.gravity = parseGravityComponent(property.value)
            "layout_marginTop" -> setLayoutMarginLocal(frameLayout, property)
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
        }
    }

    private fun setLayoutMarginLocal(view: View, property: PageLayoutViewProperty) {
        val layoutParams = view.layoutParams
        val updatedParams = LinearLayout.LayoutParams(layoutParams.width, layoutParams.height)
        when(property.name){
            "layout_marginTop" -> updatedParams.topMargin = getDigitsFromProperty(property.value).toInt()
            "layout_marginBottom" -> updatedParams.bottomMargin = getDigitsFromProperty(property.value).toInt()
            "layout_marginRight" -> updatedParams.rightMargin = getDigitsFromProperty(property.value).toInt()
            "layout_marginLeft" -> updatedParams.leftMargin = getDigitsFromProperty(property.value).toInt()
        }
    }

    private fun getDigitsFromProperty(property: String): String {
        return property.takeWhile { it.isDigit() }
    }

    /**
     * Sets background color
     *
     * @param frameLayout frame layout
     * @param value value
     */
    private fun setBackgroundColor(frameLayout: RelativeLayout, value: String) {
        val color = getColor(value)
        if (color != null) {
            frameLayout.setBackgroundColor(color)
        }
    }
}