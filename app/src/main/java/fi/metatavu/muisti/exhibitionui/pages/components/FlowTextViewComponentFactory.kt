package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.View
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import uk.co.deanwild.flowtextview.FlowTextView

/**
 * Component factory for flow text view components
 */
class FlowTextViewComponentFactory : AbstractComponentFactory<MuistiFlowTextView>() {
    override val name: String
        get() = "FlowTextView"

    override fun buildComponent(buildContext: ComponentBuildContext): MuistiFlowTextView {
        val flowTextView = MuistiFlowTextView(buildContext.context)
        setId(flowTextView, buildContext.pageLayoutView)
        flowTextView.setSpacingMultiplier(1f)

        val parent = buildContext.parents.lastOrNull()
        flowTextView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, flowTextView, it)
        }

        return flowTextView
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: MuistiFlowTextView, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "textColor" -> setTextColor(view, property.value)
                "text" -> setText(buildContext, view, property.value)
                "textAlignment" -> setTextAlignment(view, property.value)
                "textSize" -> setTextSize(view, property.value)
                "gravity" -> setGravity(view, property.value)
                "typeface" -> setTypeface(buildContext, view)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(FlowTextViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Set text alignment
     *
     * @param flowTextView text view
     * @param value value
     */
    private fun setTextAlignment(flowTextView: FlowTextView, value: String) {
        val alignment = resolveTextAlignment(value)
        alignment ?: return
        flowTextView.textAlignment = alignment
        if (alignment == View.TEXT_ALIGNMENT_CENTER) {
            flowTextView.gravity = Gravity.CENTER
        }
    }

    /**
     * Sets text size
     *
     * @param flowTextView text view
     * @param value value
     */
    private fun setTextSize(flowTextView: FlowTextView, value: String) {
        val px = stringToPx(value)
        px ?: return
        flowTextView.setTextSize(px)
    }

    /**
     * Sets gravity property
     *
     * @param flowTextView text view component
     * @param value value
     */
    private fun setGravity(flowTextView: FlowTextView, value: String) {
        val gravity = parseGravity(value)
        gravity ?: return
        flowTextView.gravity = gravity
    }

    /**
     * Sets text color property
     *
     * @param flowTextView text view component
     * @param value value
     */
    private fun setTextColor(flowTextView: FlowTextView, value: String) {
        val color = getColor(value)
        color ?: return
        flowTextView.setTextColor(color)
    }

    /**
     * Sets text view text
     *
     * @param buildContext build context
     * @param flowTextView text view component
     * @param value value
     */
    private fun setText(buildContext: ComponentBuildContext, flowTextView: FlowTextView, value: String) {
        val html = Html.fromHtml(getResourceData(buildContext, value), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        flowTextView.text = html
    }

    /**
     * Sets flow text view typeface
     *
     * @param buildContext build context
     * @param flowTextView flow text view
     */
    private fun setTypeface(buildContext: ComponentBuildContext, flowTextView: FlowTextView) {
        val offlineFile = getResourceOfflineFile(buildContext, "typeface")
        offlineFile ?: return
        flowTextView.setTypeface(Typeface.createFromFile(offlineFile))
    }
}

/**
 * FlowTextView component that extends original class and adds support for defining spacing multiplier
 *
 * @constructor constructor
 *
 * @param context context
 */
class MuistiFlowTextView(context: Context): FlowTextView(context) {

    /**
     * Sets spacing multiplier
     *
     * @param multipler spacing multiplier
     */
    fun setSpacingMultiplier(multipler: Float){
        val field = javaClass.superclass?.getDeclaredField("mSpacingMult")!!
        field.isAccessible = true
        field.set(this, multipler)
    }

}