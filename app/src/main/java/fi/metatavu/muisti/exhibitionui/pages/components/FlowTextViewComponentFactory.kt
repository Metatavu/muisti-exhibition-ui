package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType
import uk.co.deanwild.flowtextview.FlowTextView

/**
 * Component factory for relative layout components
 */
class FlowTextViewComponentFactory : AbstractComponentFactory<MuistiFlowTextView>() {

    override val name: String
        get() = "FlowTextView"

    @SuppressLint("ResourceType")
    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): MuistiFlowTextView {
        val flowTextView = MuistiFlowTextView(context)
        flowTextView.setSpacingMultiplier(1f)
        flowTextView.id = 1850

        val parent = parents.lastOrNull()
        flowTextView.layoutParams = getInitialLayoutParams(parent)

        properties.forEach {
            this.setProperty(parent, flowTextView, it, resources)
        }
        return flowTextView
    }

    /**
     * Sets a property
     *
     * @param flowTextView flow text view
     * @param property property
     */
    private fun setProperty(parent: View?, flowTextView: MuistiFlowTextView, property: PageLayoutViewProperty, resources: Array<ExhibitionPageResource>) {
        Log.d(javaClass.name, "Setting property ${property.name} to ${property.value}")

        when(property.name) {
            "height" -> setHeight(flowTextView, property.value)
            "textColor" -> setTextColor(flowTextView, property.value)
            "text" -> setText(flowTextView, resources, property.value)
            "textAlignment" -> setTextAlignment(flowTextView, property.value)
            "textSize" -> setTextSize(flowTextView, property.value)
            "gravity" -> setGravity(flowTextView, property.value)
            "layout_width" -> setLayoutWidth(parent, flowTextView, property)
            "layout_height" -> setLayoutHeight(parent, flowTextView, property)
            "background" -> setBackgroundColor(flowTextView, property.value)
            "paddingLeft" -> flowTextView.setPadding(property.value.toInt(), flowTextView.paddingTop, flowTextView.paddingRight, flowTextView.paddingBottom)
            "paddingTop" -> flowTextView.setPadding(flowTextView.paddingLeft, property.value.toInt(), flowTextView.paddingRight, flowTextView.paddingBottom)
            "paddingRight" -> flowTextView.setPadding(flowTextView.paddingLeft, flowTextView.paddingTop, property.value.toInt(), flowTextView.paddingBottom)
            "paddingBottom" -> flowTextView.setPadding(flowTextView.paddingLeft, flowTextView.paddingTop, flowTextView.paddingRight, property.value.toInt())
            "layout_gravity" -> setLayoutGravity(flowTextView, property.value)
            "layout_marginTop" -> setLayoutMargin(parent, flowTextView, property)
            "layout_marginBottom" -> setLayoutMargin(parent, flowTextView, property)
            "layout_marginRight" -> setLayoutMargin(parent, flowTextView, property)
            "layout_marginLeft" -> setLayoutMargin(parent, flowTextView, property)
            else -> Log.d(javaClass.name, "Property ${property.name} not supported")
        }
    }

    /**
     * Sets background color
     *
     * @param flowTextView frame layout
     * @param value value
     */
    private fun setBackgroundColor(flowTextView: MuistiFlowTextView, value: String) {
        val color = getColor(value)
        if (color != null) {
            flowTextView.setBackgroundColor(color)
        }
    }

    /**
     * Set text alignment
     *
     * @param textView text view
     * @param value value
     */
    private fun setTextAlignment(textView: MuistiFlowTextView, value: String) {
        val alignment = resolveTextAlignment(value)
        alignment ?: return
        textView.textAlignment = alignment
        if (alignment == View.TEXT_ALIGNMENT_CENTER) {
            textView.gravity = Gravity.CENTER
        }
    }

    /**
     * Sets text size
     *
     * @param textView text view
     * @param value value
     */
    private fun setTextSize(textView: MuistiFlowTextView, value: String) {
        val sp = getSp(value)
        sp ?: return
        textView.setTextSize(sp)
    }

    /**
     * Sets gravity property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setGravity(textView: MuistiFlowTextView, value: String) {
        val gravity = parseGravity(value)
        gravity ?: return
        textView.gravity = gravity
    }

    /**
     * Sets text color property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setTextColor(textView: MuistiFlowTextView, value: String) {
        val color = getColor(value)
        color ?: return
        textView.setTextColor(color)
    }


    /**
     * Sets height property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setHeight(textView: MuistiFlowTextView, value: String) {
        val dps = getDps(value)

        if (dps != null) {
            //textView.height = dps
        }
    }


    /**
     * Sets text view text
     *
     * @param textView text view component
     * @param resources resources
     * @param value value
     */
    private fun setText(textView: MuistiFlowTextView, resources: Array<ExhibitionPageResource>, value: String) {
        textView.text = getResource(resources, value)
    }
}

class MuistiFlowTextView(context: Context): FlowTextView(context) {
    fun setSpacingMultiplier(multipler: Float){
        val field = javaClass?.superclass?.getDeclaredField("mSpacingMult")!!
        field.isAccessible = true
        field.set(this, multipler)
    }
}