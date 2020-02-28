package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewPropertyType

class LinearLayoutComponentFactory : ComponentFactory<LinearLayout>{
    override val name: String
        get() = "LinearLayout"

    override fun buildComponent(context: Context, properties: Array<ExhibitionPageLayoutViewProperty>): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            when(it.name) {
                "layout_width" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number ->  linearLayout.layoutParams.width = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> linearLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> linearLayout.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "layout_height" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number ->  linearLayout.layoutParams.height = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> linearLayout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> linearLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "background" -> linearLayout.setBackgroundColor(Color.parseColor(it.value))
                "paddingLeft" -> linearLayout.setPadding(it.value.toInt(), linearLayout.paddingTop, linearLayout.paddingRight, linearLayout.paddingBottom)
                "paddingTop" -> linearLayout.setPadding(linearLayout.paddingLeft, it.value.toInt(), linearLayout.paddingRight, linearLayout.paddingBottom)
                "paddingRight" -> linearLayout.setPadding(linearLayout.paddingLeft, linearLayout.paddingTop, it.value.toInt(), linearLayout.paddingBottom)
                "paddingBottom" -> linearLayout.setPadding(linearLayout.paddingLeft, linearLayout.paddingTop, linearLayout.paddingRight, it.value.toInt())
                "orientation" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number -> linearLayout.orientation = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string -> when(it.value) {
                        "horizontal" -> linearLayout.orientation = LinearLayout.HORIZONTAL
                        "vertical" -> linearLayout.orientation = LinearLayout.VERTICAL
                    }
                    else -> linearLayout.orientation = LinearLayout.HORIZONTAL
                }
                "layout_gravity" -> when(it.value){
                    "center" -> linearLayout.gravity = Gravity.CENTER
                    "center_vertical" -> linearLayout.gravity = Gravity.CENTER_VERTICAL
                    "center_horizontal" -> linearLayout.gravity = Gravity.CENTER_HORIZONTAL
                    "bottom" -> linearLayout.gravity = Gravity.BOTTOM
                    "bottom|center_horizontal" -> {
                        linearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
                        linearLayout.setVerticalGravity(Gravity.BOTTOM)
                    }
                }
                "tag" -> linearLayout.tag = it.value
                else -> Log.d(javaClass.name, "Property ${it.name} not supported")
            }
        }
        return linearLayout
    }
}