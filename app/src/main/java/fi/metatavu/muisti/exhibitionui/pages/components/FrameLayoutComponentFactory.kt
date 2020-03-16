package fi.metatavu.muisti.exhibitionui.pages.components

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for frame layout components
 */
class FrameLayoutComponentFactory : AbstractComponentFactory<FrameLayout>() {

    override val name: String
        get() = "FrameLayout"

    override fun buildComponent(buildContext: ComponentBuildContext): FrameLayout {
        val frameLayout = FrameLayout(buildContext.context)
        setId(frameLayout, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        frameLayout.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(parent, frameLayout, it)
        }

        return frameLayout
    }

    /**
     * Sets a property
     *
     * @param frameLayout frame layout
     * @param property property to be set
     */
    private fun setProperty(parent: View?, frameLayout: FrameLayout, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "layout_width" -> setLayoutWidth(parent, frameLayout, property)
                "layout_height" -> setLayoutHeight(parent, frameLayout, property)
                "background" -> setBackgroundColor(frameLayout, property.value)
                "paddingLeft" -> frameLayout.setPadding(property.value.toInt(), frameLayout.paddingTop, frameLayout.paddingRight, frameLayout.paddingBottom)
                "paddingTop" -> frameLayout.setPadding(frameLayout.paddingLeft, property.value.toInt(), frameLayout.paddingRight, frameLayout.paddingBottom)
                "paddingRight" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, property.value.toInt(), frameLayout.paddingBottom)
                "paddingBottom" -> frameLayout.setPadding(frameLayout.paddingLeft, frameLayout.paddingTop, frameLayout.paddingRight, property.value.toInt())
                else -> Log.d(javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(FrameLayoutComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets background color
     *
     * @param frameLayout frame layout
     * @param value value
     */
    private fun setBackgroundColor(frameLayout: FrameLayout, value: String) {
        val color = getColor(value)
        if (color != null) {
            frameLayout.setBackgroundColor(color)
        }
    }

}