package fi.metatavu.muisti.exhibitionui.pages.components

import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty


/**
 * Component factory for touchable opacity
 */
class TouchableOpacityComponentFactory : AbstractComponentFactory<Button>() {

    override val name: String
        get() = "TouchableOpacity"

    override fun buildComponent(buildContext: ComponentBuildContext): Button {
        val button = Button(buildContext.context)
        setupView(buildContext, button)

        val parent = buildContext.parents.lastOrNull()
        button.layoutParams = getInitialLayoutParams(parent)

        val outValue = TypedValue()
        buildContext.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        button.setBackgroundResource(outValue.resourceId)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, button, it )
        }

        return button
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: Button, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "width" -> setWidth(view, property.value)
                "height" -> setHeight(view, property.value)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(TouchableOpacityComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets touchable opacity width
     *
     * @param button button
     * @param value value
     */
    private fun setWidth(button: Button, value: String?) {
        val px = stringToPx(value)
        px ?: return
        button.layoutParams.width = px.toInt()
    }

    /**
     * Sets touchable opacity height
     *
     * @param button button
     * @param value value
     */
    private fun setHeight(button: Button, value: String) {
        val px = stringToPx(value)
        px ?: return
        button.layoutParams.height = px.toInt()
    }
}