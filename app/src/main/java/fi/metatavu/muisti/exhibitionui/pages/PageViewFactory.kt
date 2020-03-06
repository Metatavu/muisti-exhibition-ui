package fi.metatavu.muisti.exhibitionui.pages

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutView
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewPropertyType
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.pages.components.*
import java.util.*

class PageViewFactory {
    private val imps = mutableListOf<ComponentFactory<*>>()

    init {
        imps.add(TextViewComponentFactory())
        imps.add(ButtonComponentFactory())
        imps.add(ImageViewComponentFactory())
        imps.add(LinearLayoutComponentFactory())
    }


    fun setLayoutToMap(layout: ExhibitionPageLayoutView){
        val context = ExhibitionUIApplication.instance.applicationContext
        val completeLayout = getLayout(layout, context)

        PageLayoutContainer.set(layout.id, completeLayout)
    }

    private fun getLayout(layout: ExhibitionPageLayoutView, context: Context) : ViewGroup {
        val factory = imps.find { it.name == layout.widget }
        val root = factory?.buildComponent(context, layout.properties) as ViewGroup

        layout.children.forEach {
            if(it.children.isNotEmpty()){
                root.addView(getLayout(it, context))
            } else {
                val childView = getView(it, context)
                root.addView(childView)
            }
        }
        return root
    }

    private fun getView(layout: ExhibitionPageLayoutView, context: Context) : View? {
        imps.forEach {
            if(layout.widget == it.name){
                return it.buildComponent(context, layout.properties)
            }
        }
        return null
    }
}