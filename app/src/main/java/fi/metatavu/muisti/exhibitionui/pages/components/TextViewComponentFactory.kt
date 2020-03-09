package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType

/**
 * Component factory for text view components
 */
class TextViewComponentFactory : AbstractComponentFactory<TextView>() {
    override val name: String
        get() = "TextView"

    override fun buildComponent(context: Context, parents: Array<View>, properties: Array<PageLayoutViewProperty>): TextView {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            when(it.name) {
                "layout_width" -> when(it.type){
                    PageLayoutViewPropertyType.number ->  textView.layoutParams.width = it.value.toInt()
                    PageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> textView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> textView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "layout_height" -> when(it.type){
                    PageLayoutViewPropertyType.number ->  textView.layoutParams.height = it.value.toInt()
                    PageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> textView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> textView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "width" -> textView.width = it.value.toInt()
                "height" -> textView.height = it.value.toInt()
                "textColor" -> textView.setTextColor(Color.parseColor(it.value))
                // "textSize" -> textView.textSize = it.value.toFloat()
                "text" -> textView.text = it.value
                "textStyle" -> when(it.value){
                    "bold" -> textView.typeface = Typeface.DEFAULT_BOLD
                    "normal" -> textView.typeface = Typeface.DEFAULT

                }
                "background" -> textView.setBackgroundColor(Color.parseColor(it.value))
                "paddingLeft" -> textView.setPadding(it.value.toInt(), textView.paddingTop, textView.paddingRight, textView.paddingBottom)
                "paddingTop" -> textView.setPadding(textView.paddingLeft, it.value.toInt(), textView.paddingRight, textView.paddingBottom)
                "paddingRight" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, it.value.toInt(), textView.paddingBottom)
                "paddingBottom" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, textView.paddingRight, it.value.toInt())
                "tag" -> textView.tag = it.value
                "layout_gravity" -> when(it.value){
                    "center" -> textView.gravity = Gravity.CENTER
                    "center_vertical" -> textView.gravity = Gravity.CENTER_VERTICAL
                    "center_horizontal" -> textView.gravity = Gravity.CENTER_HORIZONTAL
                    "bottom" -> textView.gravity = Gravity.BOTTOM
                }
                else -> Log.d(javaClass.name, "Property ${it.name} not supported")
            }
        }
        return textView
    }
}