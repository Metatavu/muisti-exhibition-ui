package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType

/**
 * Component factory for relative layout components
 */
class RelativeLayoutComponentFactory : AbstractComponentFactory<RelativeLayout>() {

    override val name: String
        get() = "RelativeLayout"

    override fun buildComponent(context: Context, parents: Array<View>, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): RelativeLayout {
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
            "layout_width" -> when(property.type){
                PageLayoutViewPropertyType.number ->  frameLayout.layoutParams.width = property.value.toInt()
                PageLayoutViewPropertyType.string ->  when(property.value){
                    "match_parent" -> frameLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    "wrap_content" -> frameLayout.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            "layout_height" -> when(property.type){
                PageLayoutViewPropertyType.number ->  frameLayout.layoutParams.height = property.value.toInt()
                PageLayoutViewPropertyType.string ->  when(property.value){
                    "match_parent" -> frameLayout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    "wrap_content" -> frameLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            "background" -> setBackgroundColor(frameLayout, property.value)
            "paddingLeft" -> frameLayout.setPadding(property.value.toInt(), frameLayout.paddingTop, frameLayout.paddingRight, frameLayout.paddingBottom)
            "paddingTop" -> frameLayout.setPadding(frameLayout.paddingLeft, property.value.toInt(), frameLayout.paddingRight, frameLayout.paddingBottom)
            "paddingRight" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, property.value.toInt(), frameLayout.paddingBottom)
            "paddingBottom" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, frameLayout.paddingRight, property.value.toInt())
            "tag" -> frameLayout.tag = property.value
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
        }
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