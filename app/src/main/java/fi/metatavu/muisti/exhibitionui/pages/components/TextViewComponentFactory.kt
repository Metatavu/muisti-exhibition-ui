package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType

/**
 * Component factory for text view components
 */
class TextViewComponentFactory : AbstractComponentFactory<TextView>() {
    override val name: String
        get() = "TextView"

    override fun buildComponent(context: Context, parents: Array<View>, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): TextView {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        properties.forEach {
            this.setProperty(textView, resources, it)
        }

        return textView
    }

    /**
     * Sets view text property
     *
     * @param textView text component
     * @param property property
     */
    private fun setProperty(textView: TextView, resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        when(property.name) {
            "layout_width" -> when(property.type){
                PageLayoutViewPropertyType.number ->  textView.layoutParams.width = property.value.toInt()
                PageLayoutViewPropertyType.string ->  when(property.value){
                    "match_parent" -> textView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    "wrap_content" -> textView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            "layout_height" -> when(property.type){
                PageLayoutViewPropertyType.number ->  textView.layoutParams.height = property.value.toInt()
                PageLayoutViewPropertyType.string ->  when(property.value){
                    "match_parent" -> textView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    "wrap_content" -> textView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            "width" -> textView.width = property.value.toInt()
            "height" -> textView.height = property.value.toInt()
            "textColor" -> textView.setTextColor(Color.parseColor(property.value))
            // "textSize" -> textView.textSize = property.value.toFloat()
            "text" -> setText(textView, resources, property.value)
            "textStyle" -> when(property.value){
                "bold" -> textView.typeface = Typeface.DEFAULT_BOLD
                "normal" -> textView.typeface = Typeface.DEFAULT

            }
            "background" -> textView.setBackgroundColor(Color.parseColor(property.value))
            "paddingLeft" -> textView.setPadding(property.value.toInt(), textView.paddingTop, textView.paddingRight, textView.paddingBottom)
            "paddingTop" -> textView.setPadding(textView.paddingLeft, property.value.toInt(), textView.paddingRight, textView.paddingBottom)
            "paddingRight" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, property.value.toInt(), textView.paddingBottom)
            "paddingBottom" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, textView.paddingRight, property.value.toInt())
            "tag" -> textView.tag = property.value
            "layout_gravity" -> when(property.value){
                "center" -> textView.gravity = Gravity.CENTER
                "center_vertical" -> textView.gravity = Gravity.CENTER_VERTICAL
                "center_horizontal" -> textView.gravity = Gravity.CENTER_HORIZONTAL
                "bottom" -> textView.gravity = Gravity.BOTTOM
            }
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
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