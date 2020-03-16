package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.pages.PageViewActivator

/**
 * Component factory for buttons
 */
class ButtonComponentFactory : AbstractComponentFactory<Button>() {

    override val name: String
        get() = "Button"

    override fun buildComponent(context: Context, parents: Array<View>, pageLayoutView: PageLayoutView, resources: Array<ExhibitionPageResource>, activators: MutableList<PageViewActivator>): Button {
        val button = Button(context)
        setId(button, pageLayoutView)

        val parent = parents.lastOrNull()
        button.layoutParams = getInitialLayoutParams(parent)

        pageLayoutView.properties.forEach {
            this.setProperty(parent, button, it )
        }

        return button
    }

    /**
     * Sets button property
     *
     * @param parent parent view
     * @param button button
     * @param property property
     */
    private fun setProperty(parent: View?, button: Button, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "layout_width" -> setLayoutWidth(parent, button, property)
                "layout_height" -> setLayoutHeight(parent, button, property)
                "width" -> setWidth(button, property.value)
                "height" -> setHeight(button, property.value)
                "textColor" -> button.setTextColor(Color.parseColor(property.value))
                "textSize" -> button.textSize = property.value.toFloat()
                "text" -> button.text = property.value
                "textStyle" -> setTextStyle(property, button)
                "layout_gravity" -> setLayoutGravity(button, property.value)
                "background" -> button.setBackgroundColor(Color.parseColor(property.value))
                "paddingLeft" -> button.setPadding(
                    property.value.toInt(),
                    button.paddingTop,
                    button.paddingRight,
                    button.paddingBottom
                )
                "paddingTop" -> button.setPadding(
                    button.paddingLeft,
                    property.value.toInt(),
                    button.paddingRight,
                    button.paddingBottom
                )
                "paddingRight" -> button.setPadding(
                    button.paddingLeft,
                    button.paddingTop,
                    property.value.toInt(),
                    button.paddingBottom
                )
                "paddingBottom" -> button.setPadding(
                    button.paddingLeft,
                    button.paddingTop,
                    button.paddingRight,
                    property.value.toInt()
                )

                "layout_marginTop" -> setLayoutMargin(parent, button, property)
                "layout_marginBottom" -> setLayoutMargin(parent, button, property)
                "layout_marginRight" -> setLayoutMargin(parent, button, property)
                "layout_marginLeft" -> setLayoutMargin(parent, button, property)
                else -> Log.d(ButtonComponentFactory::javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(ButtonComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets text styles
     *
     * @param property
     * @param button
     */
    private fun setTextStyle(property: PageLayoutViewProperty, button: Button) {
        when (property.value) {
            "bold" -> button.typeface = Typeface.DEFAULT_BOLD
            "normal" -> button.typeface = Typeface.DEFAULT
        }
    }

    /**
     * Sets button width
     *
     * @param button button
     * @param value value
     */
    private fun setWidth(button: Button, value: String?) {
        val dps = getDps(value)
        dps ?: return
        button.layoutParams.width = dps
    }

    /**
     * Sets button height
     *
     * @param button button
     * @param value value
     */
    private fun setHeight(button: Button, value: String) {
        val dps = getDps(value)
        dps ?: return
        button.layoutParams.height = dps
    }
}