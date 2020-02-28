package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.widget.TextView
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewPropertyType
import fi.metatavu.muisti.exhibitionui.R

class ImageViewComponentFactory : ComponentFactory<ImageView>{
    override val name: String
        get() = "ImageView"

    override fun buildComponent(context: Context, properties: Array<ExhibitionPageLayoutViewProperty>): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            when(it.name) {
                "layout_width" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number ->  imageView.layoutParams.width = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> imageView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> imageView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "layout_height" -> when(it.type){
                    ExhibitionPageLayoutViewPropertyType.number ->  imageView.layoutParams.height = it.value.toInt()
                    ExhibitionPageLayoutViewPropertyType.string ->  when(it.value){
                        "match_parent" -> imageView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> imageView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "width" -> imageView.layoutParams.width = it.value.toInt()
                "height" -> imageView.layoutParams.height = it.value.toInt()
                "background" -> imageView.setBackgroundColor(Color.parseColor(it.value))
                "paddingLeft" -> imageView.setPadding(it.value.toInt(), imageView.paddingTop, imageView.paddingRight, imageView.paddingBottom)
                "paddingTop" -> imageView.setPadding(imageView.paddingLeft, it.value.toInt(), imageView.paddingRight, imageView.paddingBottom)
                "paddingRight" -> imageView.setPadding(imageView.paddingLeft, imageView.paddingTop, it.value.toInt(), imageView.paddingBottom)
                "paddingBottom" -> imageView.setPadding(imageView.paddingLeft, imageView.paddingTop, imageView.paddingRight, it.value.toInt())
                "src" -> imageView.setImageResource(R.drawable.cat)
                "tag" -> imageView.tag = it.value
                else -> Log.d(javaClass.name, "Property ${it.name} not supported")
            }
        }
        return imageView
    }
}