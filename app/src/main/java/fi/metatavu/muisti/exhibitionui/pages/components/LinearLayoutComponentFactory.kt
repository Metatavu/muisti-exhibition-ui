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
        linearLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        properties.forEach {
            when(it.name) {
                "layout_width" -> setLayoutWidths(linearLayout, it)
                "layout_height" -> setLayoutHeights(linearLayout, it)
                "background" -> setBackgroundColor(linearLayout, it.value)
                "paddingLeft" -> linearLayout.setPadding(
                    it.value.toInt(),
                    linearLayout.paddingTop,
                    linearLayout.paddingRight,
                    linearLayout.paddingBottom
                )

                "paddingTop" -> linearLayout.setPadding(
                    linearLayout.paddingLeft,
                    it.value.toInt(),
                    linearLayout.paddingRight,linearLayout.paddingBottom
                )

                "paddingRight" -> linearLayout.setPadding(
                    linearLayout.paddingLeft,
                    linearLayout.paddingTop,
                    it.value.toInt(),
                    linearLayout.paddingBottom
                )

                "paddingBottom" -> linearLayout.setPadding(
                    linearLayout.paddingLeft,
                    linearLayout.paddingTop, linearLayout.paddingRight,
                    it.value.toInt()
                )

                "orientation" -> setOrientation(linearLayout, it)
                "layout_gravity" -> setGravity(linearLayout, it)
                "tag" -> linearLayout.tag = it.value
                else -> Log.d(javaClass.name, "Property ${it.name} not supported")
            }
        }

        return linearLayout
    }

    /**
     * Sets gravities
     * 
     * @param linearLayout linear layout
     * @param pageLayoutViewProperty PageLayoutViewProperty to be set
     */
    private fun setGravity(linearLayout: LinearLayout, pageLayoutViewProperty: PageLayoutViewProperty) {
        when (pageLayoutViewProperty.value) {
            "center" -> linearLayout.gravity = Gravity.CENTER
            "center_vertical" -> linearLayout.gravity = Gravity.CENTER_VERTICAL
            "center_horizontal" -> linearLayout.gravity = Gravity.CENTER_HORIZONTAL
            "bottom" -> linearLayout.gravity = Gravity.BOTTOM
            "bottom|center_horizontal" -> {
                linearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
                linearLayout.setVerticalGravity(Gravity.BOTTOM)
            }
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