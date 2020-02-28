package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewPropertyType

class ButtonComponentFactory : ComponentFactory<Button>{
    override val name: String
        get() = "Button"

    override fun buildComponent(context: Context, properties: Array<ExhibitionPageLayoutViewProperty>): Button {
        val button = Button(context)
        button.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            when(it.name) {
                "layout_width" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number ->  button.layoutParams.width = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> button.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> button.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "layout_height" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number ->  button.layoutParams.height = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> button.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> button.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "width" -> button.width = it.value.toInt()
                "height" -> button.height = it.value.toInt()
                "textColor" -> button.setTextColor(Color.parseColor(it.value))
                "textSize" -> button.textSize = it.value.toFloat()
                "text" -> button.text = it.value
                "textStyle" -> when(it.value){
                    "bold" -> button.typeface = Typeface.DEFAULT_BOLD
                    "normal" -> button.typeface = Typeface.DEFAULT
                }
                "layout_gravity" -> when(it.value){
                    "center" -> button.gravity = Gravity.CENTER
                    "center_vertical" -> button.gravity = Gravity.CENTER_VERTICAL
                    "center_horizontal" -> button.gravity = Gravity.CENTER_HORIZONTAL
                    "bottom" -> button.gravity = Gravity.BOTTOM
                }
                "background" -> button.setBackgroundColor(Color.parseColor(it.value))
                "paddingLeft" -> button.setPadding(it.value.toInt(), button.paddingTop, button.paddingRight, button.paddingBottom)
                "paddingTop" -> button.setPadding(button.paddingLeft, it.value.toInt(), button.paddingRight, button.paddingBottom)
                "paddingRight" -> button.setPadding(button.paddingLeft, button.paddingTop, it.value.toInt(), button.paddingBottom)
                "paddingBottom" -> button.setPadding(button.paddingLeft, button.paddingTop, button.paddingRight, it.value.toInt())
                "tag" -> button.tag = it.value
                else -> Log.d(javaClass.name, "Property ${it.name} not supported")
            }
        }
        return button
    }
}