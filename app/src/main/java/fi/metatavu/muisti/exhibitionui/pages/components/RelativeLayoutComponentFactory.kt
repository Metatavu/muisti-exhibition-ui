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
        setId(frameLayout, id)

        val parent = parents.lastOrNull()
        frameLayout.layoutParams = getInitialLayoutParams(parent)

        properties.forEach {
            this.setProperty(parent, frameLayout, it)
        }

        return frameLayout
    }

    /**
     * Sets a property
     *
     * @param frameLayout frame layout
     * @param property property
     */
    private fun setProperty(parent: View?, frameLayout: RelativeLayout, property: PageLayoutViewProperty) {
        when(property.name) {
            "layout_width" -> setLayoutWidth(parent, frameLayout, property)
            "layout_height" -> setLayoutHeight(parent, frameLayout, property)
            "background" -> setBackgroundColor(frameLayout, property.value)
            "paddingLeft" -> frameLayout.setPadding(property.value.toInt(), frameLayout.paddingTop, frameLayout.paddingRight, frameLayout.paddingBottom)
            "paddingTop" -> frameLayout.setPadding(frameLayout.paddingLeft, property.value.toInt(), frameLayout.paddingRight, frameLayout.paddingBottom)
            "paddingRight" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, property.value.toInt(), frameLayout.paddingBottom)
            "paddingBottom" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, frameLayout.paddingRight, property.value.toInt())
            "layout_gravity" -> setLayoutGravity(frameLayout, property.value)
            "layout_marginTop" -> setLayoutMargin(parent, frameLayout, property)
            "layout_marginBottom" -> setLayoutMargin(parent, frameLayout, property)
            "layout_marginRight" -> setLayoutMargin(parent, frameLayout, property)
            "layout_marginLeft" -> setLayoutMargin(parent, frameLayout, property)
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