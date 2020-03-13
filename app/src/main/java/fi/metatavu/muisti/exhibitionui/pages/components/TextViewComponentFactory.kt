package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
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

    override fun buildComponent(context: Context, parents: Array<View>, id: String, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): TextView {
        val textView = TextView(context)
        setId(textView, id)

        val parent = parents.lastOrNull()
        textView.layoutParams = getInitialLayoutParams(parent)

        properties.forEach {
            this.setProperty(parent, textView, resources, it)
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
            "layout_gravity" -> setLayoutGravity(textView, property.value)
            "layout_marginTop" -> setLayoutMargin(parent, textView, property)
            "layout_marginBottom" -> setLayoutMargin(parent, textView, property)
            "layout_marginRight" -> setLayoutMargin(parent, textView, property)
            "layout_marginLeft" -> setLayoutMargin(parent, textView, property)
            "layout_toRightOf" -> setLayoutOf(textView, property)
            else -> Log.d(this.javaClass.name, "Property ${property.name} not supported")
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
        val sp = getSp(value)
        sp ?: return
        textView.textSize = sp
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
        val dps = getDps(value)

        if (dps != null) {
            textView.width = dps
        }
    }

    /**
     * Sets height property
     *
     * @param textView text view component
     * @param value value
     */
    private fun setHeight(textView: TextView, value: String) {
        val dps = getDps(value)

        if (dps != null) {
            textView.height = dps
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
        textView.text = getResource(resources, value)
    }
}