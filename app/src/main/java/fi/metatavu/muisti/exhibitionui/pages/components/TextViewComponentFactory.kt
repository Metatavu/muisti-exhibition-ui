package fi.metatavu.muisti.exhibitionui.pages.components

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for text view components
 */
class TextViewComponentFactory : AbstractComponentFactory<TextView>() {
    override val name: String
        get() = "TextView"

    override fun buildComponent(buildContext: ComponentBuildContext): TextView {
        val textView = TextView(buildContext.context)
        setId(textView, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        textView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(parent, textView, buildContext.page.resources, it)
        }

        return textView
    }

    /**
     * Sets view text property
     *
     * @param textView text component
     * @param resources resources
     * @param property property
     */
    private fun setProperty(parent: View?, textView: TextView, resources: Array<ExhibitionPageResource>, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "layout_width" -> setLayoutWidth(parent, textView, property)
                "layout_height" -> setLayoutHeight(parent, textView, property)
                "width" -> setWidth(textView, property.value)
                "height" -> setHeight(textView, property.value)
                "textColor" -> setTextColor(textView, property.value)
                "text" -> setText(textView, resources, property.value)
                "textStyle" -> setTextStyle(textView, property.value)
                "textAlignment" -> setTextAlignment(textView, property.value)
                "textSize" -> setTextSize(textView, property.value)
                "gravity" -> setGravity(textView, property.value)
                "background" -> textView.setBackgroundColor(Color.parseColor(property.value))
                "paddingLeft" -> textView.setPadding(property.value.toInt(), textView.paddingTop, textView.paddingRight, textView.paddingBottom)
                "paddingTop" -> textView.setPadding(textView.paddingLeft, property.value.toInt(), textView.paddingRight, textView.paddingBottom)
                "paddingRight" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, property.value.toInt(), textView.paddingBottom)
                "paddingBottom" -> textView.setPadding(textView.paddingLeft, textView.paddingTop, textView.paddingRight, property.value.toInt())
                "layout_gravity" -> setLayoutGravities(property, textView)
                "layout_marginTop" -> setLayoutMargin(parent, textView, property)
                "layout_marginBottom" -> setLayoutMargin(parent, textView, property)
                "layout_marginRight" -> setLayoutMargin(parent, textView, property)
                "layout_marginLeft" -> setLayoutMargin(parent, textView, property)
                "layout_toRightOf" -> setLayoutOf(textView, property)
                else -> Log.d(this.javaClass.name, "Property ${property.name} not supported")
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
     * Sets gravities
     *
     * @param property to be set
     * @param textView text view component
     */
    private fun setLayoutGravities(property: PageLayoutViewProperty, textView: TextView) {
        when (property.value) {
            "center" -> textView.gravity = Gravity.CENTER
            "center_vertical" -> textView.gravity = Gravity.CENTER_VERTICAL
            "center_horizontal" -> textView.gravity = Gravity.CENTER_HORIZONTAL
            "bottom" -> textView.gravity = Gravity.BOTTOM
        }
    }

    /**
     * Sets text view text
     *
     * @param textView text view component
     * @param resources resources
     * @param value value
     */
    private fun setText(textView: TextView, resources: Array<ExhibitionPageResource>, value: String) {
        textView.text = getResourceData(resources, value)
    }
}