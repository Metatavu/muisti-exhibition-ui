package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty


/**
 * Component factory for text view components
 */
class TextViewComponentFactory : AbstractComponentFactory<TextView>() {
    override val name: String
        get() = "TextView"

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): TextView {
        val textView = TextView(context)
        val parent = parents.last()
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        properties.forEach {
            this.setProperty(parent, textView, resources, it)
        }

        return textView
    }

    /**
     * Sets view text property
     *
     * @param textView text component
     * @param resources resources
     * @param property property
     */
    private fun setProperty(parent: View, textView: TextView, resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        Log.d(javaClass.name, "Setting property ${property.name} to ${property.value}")

        when(property.name) {
            "layout_width" -> setLayoutWidths(textView, property)
            "layout_height" -> setLayoutHeights(textView, property)
            "width" -> textView.width = property.value.toInt()
            "height" -> textView.height = property.value.toInt()
            "textColor" -> textView.setTextColor(Color.parseColor(property.value))
            "text" -> setText(textView, resources, property.value)
            "textStyle" -> when(property.value){
                "bold" -> textView.typeface = Typeface.DEFAULT_BOLD
                "normal" -> textView.typeface = Typeface.DEFAULT
            }
            "textAlignment" -> resolveTextAlign(textView, property.value)
            "textSize" -> resolveTextSize(textView, property.value)
            "gravity" -> textView.gravity = parseGravity(property.value)
            "background" -> textView.setBackgroundColor(Color.parseColor(property.value))
            "paddingLeft" -> textView.setPadding(property.value.toInt(), textView.paddingTop, textView.paddingRight, textView.paddingBottom)
            "paddingTop" -> textView.setPadding(textView.paddingLeft, property.value.toInt(), textView.paddingRight, textView.paddingBottom)
            "paddingRight" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, property.value.toInt(), textView.paddingBottom)
            "paddingBottom" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, textView.paddingRight, property.value.toInt())
            "tag" -> textView.tag = property.value
            "layout_gravity" -> setLayoutGravities(property, textView)
            "layout_marginTop" -> setLayoutMargin(parent, textView, property)
            "layout_marginBottom" -> setLayoutMargin(parent, textView, property)
            "layout_marginRight" -> setLayoutMargin(parent, textView, property)
            "layout_marginLeft" -> setLayoutMargin(parent, textView, property)
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
        }
    }

    /**
     * Sets gravities
     *
     * @param property to be set
     * @param textView text view component
     */
    private fun setLayoutGravities(property: PageLayoutViewProperty, textView: TextView) {
        when (property.value) {
            "center" -> textView.gravity = Gravity.CENTER
            "center_vertical" -> textView.gravity = Gravity.CENTER_VERTICAL
            "center_horizontal" -> textView.gravity = Gravity.CENTER_HORIZONTAL
            "bottom" -> textView.gravity = Gravity.BOTTOM
        }
    }

    /**
     * Sets text view text
     *
     * @param textView text view component
     * @param resources resources
     * @param value value
     */
    private fun setText(textView: TextView, resources: Array<ExhibitionPageResource>, value: String) {
        textView.text = getResource(resources, value)
    }
}