package fi.metatavu.muisti.exhibitionui.pages.components

import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for text view components
 */
class TextViewComponentFactory : AbstractComponentFactory<TextView>() {
    override val name: String
        get() = "TextView"

    override fun buildComponent(buildContext: ComponentBuildContext): TextView {
        val textView = TextView(buildContext.context)
        setupView(buildContext, textView)

        val parent = buildContext.parents.lastOrNull()
        textView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, textView, it)
        }

        return textView
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: TextView, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "width" -> setWidth(view, property.value)
                "height" -> setHeight(view, property.value)
                "textColor" -> setTextColor(view, property.value)
                "text" -> setText(buildContext, view, property.value)
                "textStyle" -> setTextStyle(view, property.value)
                "textAlignment" -> setTextAlignment(view, property.value)
                "textSize" -> setTextSize(view, property.value)
                "gravity" -> setGravity(view, property.value)
                "typeface" -> setTypeface(buildContext, view)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(TextViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Set text alignment
     *
     * @param textView text view
     * @param value value
     */
    private fun setTextAlignment(textView: TextView, value: String) {
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
    private fun setTextSize(textView: TextView, value: String) {
        val px = stringToPx(value)
        px ?: return
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, px)
    }

    /**
     * Sets gravity property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setGravity(textView: TextView, value: String) {
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
    private fun setTextColor(textView: TextView, value: String) {
        val color = getColor(value)
        color ?: return
        textView.setTextColor(color)
    }

    /**
     * Sets text style property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setTextStyle(textView: TextView, value: String) {
        when (value) {
            "bold" -> textView.typeface = Typeface.DEFAULT_BOLD
            "normal" -> textView.typeface = Typeface.DEFAULT
            else -> Log.d(this.javaClass.name,"Unknown text style $value")
        }
    }

    /**
     * Sets width property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setWidth(textView: TextView, value: String) {
        val px = stringToPx(value)
        px ?: return
        textView.width = px.toInt()
    }

    /**
     * Sets height property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setHeight(textView: TextView, value: String) {
        val px = stringToPx(value)
        px ?: return
        textView.height = px.toInt()
    }

    /**
     * Sets text view text
     *
     * @param buildContext build context
     * @param textView text view component
     * @param value value
     */
    private fun setText(buildContext: ComponentBuildContext, textView: TextView, value: String?) {
        textView.text = parseHtmlResource(buildContext, value) ?: ""
    }

    /**
     * Sets text view typeface
     *
     * @param buildContext build context
     * @param view view
     */
    private fun setTypeface(buildContext: ComponentBuildContext, view: TextView) {
        val offlineFile = getResourceOfflineFile(buildContext, "typeface")
        offlineFile ?: return
        view.typeface = Typeface.createFromFile(offlineFile)
    }
}