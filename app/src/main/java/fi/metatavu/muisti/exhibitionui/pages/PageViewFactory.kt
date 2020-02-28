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


    fun setLayoutToMap(/*layout: ExhibitionPageLayoutView*/){

        val linearProperty1 = ExhibitionPageLayoutViewProperty(
            name = "layout_width",
            value = "match_parent",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val linearProperty2 = ExhibitionPageLayoutViewProperty(
            name = "layout_height",
            value = "match_parent",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val linearProperty3 = ExhibitionPageLayoutViewProperty(
            name = "gravity",
            value = "center",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val linearProperty5 = ExhibitionPageLayoutViewProperty(
            name = "orientation",
            value = "vertical",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val textProperty1 = ExhibitionPageLayoutViewProperty(
            name = "layout_width",
            value = "match_parent",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val textProperty2 = ExhibitionPageLayoutViewProperty(
            name = "layout_height",
            value = "100",
            type = ExhibitionPageLayoutViewPropertyType.number
        )
        val textProperty3 = ExhibitionPageLayoutViewProperty(
            name = "layout_gravity",
            value = "center",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val textProperty4 = ExhibitionPageLayoutViewProperty(
            name = "text",
            value = "Test Page",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val textProperty5 = ExhibitionPageLayoutViewProperty(
            name = "textSize",
            value = "20",
            type = ExhibitionPageLayoutViewPropertyType.number
        )

        val textProperty6 = ExhibitionPageLayoutViewProperty(
            name = "textColor",
            value = "#000000",
            type = ExhibitionPageLayoutViewPropertyType.number
        )

        val textProperty7 = ExhibitionPageLayoutViewProperty(
            name = "textStyle",
            value = "bold",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val bgBlue = ExhibitionPageLayoutViewProperty(
            name = "background",
            value = "#ebba75",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val bgRed = ExhibitionPageLayoutViewProperty(
            name = "background",
            value = "#a8323c",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val center = ExhibitionPageLayoutViewProperty(
            name = "layout_gravity",
            value = "center",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val vertical = ExhibitionPageLayoutViewProperty(
            name = "orientation",
            value = "vertical",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val buttonProperty = ExhibitionPageLayoutViewProperty(
            name = "layout_margin",
            value = "10",
            type = ExhibitionPageLayoutViewPropertyType.number
        )

        val buttonProperty2 = ExhibitionPageLayoutViewProperty(
            name = "layout_width",
            value = "wrap_content",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val buttonProperty3 = ExhibitionPageLayoutViewProperty(
            name = "layout_width",
            value = "wrap_content",
            type = ExhibitionPageLayoutViewPropertyType.string
        )
        val buttonProperty4 = ExhibitionPageLayoutViewProperty(
            name = "text",
            value = "Tämä on nappi A",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val buttonProperty5 = ExhibitionPageLayoutViewProperty(
            name = "text",
            value = "Tämä on nappi B",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val button2 = ExhibitionPageLayoutView(
            id = UUID.randomUUID(),
            widget =  "Button",
            properties = arrayOf(buttonProperty,buttonProperty2,buttonProperty3,buttonProperty5),
            children = emptyArray()
        )

        val button = ExhibitionPageLayoutView(
            id = UUID.randomUUID(),
            widget =  "Button",
            properties = arrayOf(buttonProperty,buttonProperty2,buttonProperty3,buttonProperty4),
            children = emptyArray()
        )

        val imageProperty1 = ExhibitionPageLayoutViewProperty(
            name = "src",
            value = "will not do anything",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val imageProperty2 = ExhibitionPageLayoutViewProperty(
            name = "layout_height",
            value = "wrap_content",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val imageProperty3 = ExhibitionPageLayoutViewProperty(
            name = "layout_width",
            value = "wrap_content",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val imageView = ExhibitionPageLayoutView(
            id = UUID.randomUUID(),
            widget =  "ImageView",
            properties = arrayOf(imageProperty1, imageProperty2, imageProperty3),
            children = emptyArray()
        )

        val textView = ExhibitionPageLayoutView(
            id = UUID.randomUUID(),
            widget =  "TextView",
            properties = arrayOf(textProperty1,textProperty2,textProperty3,textProperty4,textProperty5,textProperty6,textProperty7),
            children = emptyArray()
        )

        val bottomProperty = ExhibitionPageLayoutViewProperty(
            name = "layout_width",
            value = "match_parent",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val bottomProperty2 = ExhibitionPageLayoutViewProperty(
            name = "layout_height",
            value = "wrap_content",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val bottomProperty3 = ExhibitionPageLayoutViewProperty(
            name = "gravity",
            value = "center",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val bottomProperty4 = ExhibitionPageLayoutViewProperty(
            name = "orientation",
            value = "horizontal",
            type = ExhibitionPageLayoutViewPropertyType.string
        )

        val bottomLayout = ExhibitionPageLayoutView(
            id = UUID.randomUUID(),
            widget =  "LinearLayout",
            properties = arrayOf(bottomProperty,bottomProperty2,bottomProperty3,bottomProperty4,center),
            children = arrayOf(button,button2)
        )

        val rootLayout = ExhibitionPageLayoutView(
            id = UUID.randomUUID(),
            widget =  "LinearLayout",
            properties = arrayOf(linearProperty1, linearProperty2, linearProperty3, linearProperty5, bgBlue),
            children = arrayOf(textView, imageView, bottomLayout)
        )



        val context = ExhibitionUIApplication.instance.applicationContext
        val completeLayout = getLayout(rootLayout, context)

        PageLayoutContainer.set("2", completeLayout)
        /*
        val properties = mutableMapOf<String, String>()

        properties["width"] = "300"
        properties["height"] = "800"
        properties["background"] = "#ffffff"

        val context = ExhibitionUIApplication.instance.applicationContext
        val textView = TextViewComponentFactory().buildComponent(context, properties)

        val layout = LinearLayout(context)
        layout.gravity = Gravity.CENTER
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        layout.addView(textView, params)
        PageLayoutContainer.set("1", layout)


        val properties2 = mutableMapOf<String, String>()

        properties2["tag"] = "porkkana"
        properties2["width"] = "800"
        properties2["height"] = "300"
        properties2["background"] = "#ffb812"

        val textView2 = TextViewComponentFactory().buildComponent(context, properties2)

        val layout2 = LinearLayout(context)
        layout2.gravity = Gravity.CENTER
        val params2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        layout2.addView(textView2, params2)
        PageLayoutContainer.set("2", layout2)
        */
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