package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType

/**
 * Linear layout component factory
 */
class LinearLayoutComponentFactory : AbstractComponentFactory<LinearLayout>() {
    override val name: String
        get() = "LinearLayout"

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): LinearLayout {
        val linearLayout = LinearLayout(context)
        setId(linearLayout, id)

        val parent = parents.last()
        linearLayout.layoutParams = getInitialLayoutParams(parent)

        properties.forEach {
            setProperty(parent, linearLayout, it)
        }

        return linearLayout
    }

    /**
     * Sets a property
     *
     * @param parent parent
     * @param linearLayout linear layout
     * @param property property to be set
     */
    private fun setProperty(parent: View, linearLayout: LinearLayout, property: PageLayoutViewProperty) {
        when(property.name) {
            "layout_width" -> setLayoutWidth(parent, linearLayout, property)
            "layout_height" -> setLayoutHeight(parent, linearLayout, property)
            "background" -> setBackgroundColor(linearLayout, property.value)
            "paddingLeft" -> linearLayout.setPadding(
                property.value.toInt(),
                linearLayout.paddingTop,
                linearLayout.paddingRight,
                linearLayout.paddingBottom
            )

            "paddingTop" -> linearLayout.setPadding(
                linearLayout.paddingLeft,
                property.value.toInt(),
                linearLayout.paddingRight,linearLayout.paddingBottom
            )

            "paddingRight" -> linearLayout.setPadding(
                linearLayout.paddingLeft,
                linearLayout.paddingTop,
                property.value.toInt(),
                linearLayout.paddingBottom
            )

            "paddingBottom" -> linearLayout.setPadding(
                linearLayout.paddingLeft,
                linearLayout.paddingTop, linearLayout.paddingRight,
                property.value.toInt()
            )

            "orientation" -> setOrientation(linearLayout, property)
            "layout_gravity" -> setLayoutGravity(linearLayout, property.value)
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
        }
    }

    /**
     * Sets orientations
     * 
     * @param linearLayout linear layout
     * @param pageLayoutViewProperty property to be set
     */
    private fun setOrientation(linearLayout: LinearLayout, pageLayoutViewProperty: PageLayoutViewProperty) {
        when (pageLayoutViewProperty.type) {
            PageLayoutViewPropertyType.number -> linearLayout.orientation = pageLayoutViewProperty.value.toInt()
            PageLayoutViewPropertyType.string -> when (pageLayoutViewProperty.value) {
                "horizontal" -> linearLayout.orientation = LinearLayout.HORIZONTAL
                "vertical" -> linearLayout.orientation = LinearLayout.VERTICAL
            }
            else -> linearLayout.orientation = LinearLayout.HORIZONTAL
        }
    }

    /**
     * Sets background color
     *
     * @param linearLayout linear layout
     * @param value value
     */
    private fun setBackgroundColor(linearLayout: LinearLayout, value: String) {
        val color = getColor(value)

        if (color != null) {
            linearLayout.setBackgroundColor(color)
        }
    }
}