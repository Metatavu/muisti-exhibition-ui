package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType
import java.lang.Exception

/**
 * Component factory for buttons
 */
class ButtonComponentFactory : AbstractComponentFactory<Button>() {

    override val name: String
        get() = "Button"

    override fun buildComponent(context: Context, parents: Array<View>, resources: Array<ExhibitionPageResource>, properties: Array<PageLayoutViewProperty>): Button {
        val button = Button(context)
        val parent = parents.last()

        button.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        properties.forEach {
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
    private fun setProperty(parent: View, button: Button, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "layout_width" -> when (property.type) {
                    PageLayoutViewPropertyType.number -> setWidth(button, property.value)
                    PageLayoutViewPropertyType.string -> when (property.value) {
                        "match_parent" -> button.layoutParams.width =
                            ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> button.layoutParams.width =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "layout_height" -> when (property.type) {
                    PageLayoutViewPropertyType.number -> setHeight(button, property.value)
                    PageLayoutViewPropertyType.string -> when (property.value) {
                        "match_parent" -> button.layoutParams.height =
                            ViewGroup.LayoutParams.MATCH_PARENT
                        "wrap_content" -> button.layoutParams.height =
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
                "width" -> button.width = property.value.toInt()
                "height" -> button.height = property.value.toInt()
                "textColor" -> button.setTextColor(Color.parseColor(property.value))
                "textSize" -> button.textSize = property.value.toFloat()
                "text" -> button.text = property.value
                "textStyle" -> when (property.value) {
                    "bold" -> button.typeface = Typeface.DEFAULT_BOLD
                    "normal" -> button.typeface = Typeface.DEFAULT
                }
                "layout_gravity" -> setLayoutGravity(parent, button, property.value)
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
                "tag" -> button.tag = property.value
                else -> Log.d(ButtonComponentFactory::javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(ButtonComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
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