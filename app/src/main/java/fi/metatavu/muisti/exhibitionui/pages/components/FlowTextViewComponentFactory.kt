package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType
import uk.co.deanwild.flowtextview.FlowTextView

/**
 * Component factory for relative layout components
 */
class FlowTextViewComponentFactory : AbstractComponentFactory<FlowTextView>() {

    override val name: String
        get() = "FlowTextView"

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): FlowTextView {
        val frameLayout = FlowTextView(context)
        frameLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
            this.setProperty(frameLayout, resources,it)
        }
        return frameLayout
    }

    /**
     * Sets a property
     *
     * @param flowTextView flow Text View
     * @param property property
     */
    private fun setProperty(flowTextView: FlowTextView,  resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        Log.d(javaClass.name, "Setting property ${property.name} to ${property.value}")

        when(property.name) {
            "layout_width" -> when(property.type){
                PageLayoutViewPropertyType.number ->  flowTextView.layoutParams.width = property.value.toInt()
                PageLayoutViewPropertyType.string ->  when(property.value){
                    "match_parent" -> flowTextView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    "wrap_content" -> flowTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            "layout_height" -> when(property.type){
                PageLayoutViewPropertyType.number ->  flowTextView.layoutParams.height = property.value.toInt()
                PageLayoutViewPropertyType.string ->  when(property.value){
                    "match_parent" -> flowTextView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    "wrap_content" -> flowTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            "background" -> setBackgroundColor(flowTextView, property.value)
            "paddingLeft" -> flowTextView.setPadding(property.value.toInt(), flowTextView.paddingTop, flowTextView.paddingRight, flowTextView.paddingBottom)
            "paddingTop" -> flowTextView.setPadding(flowTextView.paddingLeft, property.value.toInt(), flowTextView.paddingRight, flowTextView.paddingBottom)
            "paddingRight" -> flowTextView.setPadding(flowTextView.paddingLeft, flowTextView.paddingTop, property.value.toInt(), flowTextView.paddingBottom)
            "paddingBottom" -> flowTextView.setPadding(flowTextView.paddingLeft, flowTextView.paddingTop, flowTextView.paddingRight, property.value.toInt())
            "text" -> setText(flowTextView, resources, property.value)
            "tag" -> flowTextView.tag = property.value
            "id" -> flowTextView.tag = property.value
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
        }
    }

    /**
     * Sets background color
     *
     * @param frameLayout frame layout
     * @param value value
     */
    private fun setBackgroundColor(frameLayout: RelativeLayout, value: String) {
        val color = getColor(value)
        if (color != null) {
            frameLayout.setBackgroundColor(color)
        }
    }

    private fun setText( flowTextView: FlowTextView, resources: Array<ExhibitionPageResource>, value: String?){
        val text = getResource(resources, value)
        if(text == null){
            flowTextView.text = value
        } else {
            flowTextView.text = text
        }
    }
}