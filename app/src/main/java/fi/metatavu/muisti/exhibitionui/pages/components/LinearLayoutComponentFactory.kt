package fi.metatavu.muisti.exhibitionui.pages.components

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType

/**
 * Linear layout component factory
 */
class LinearLayoutComponentFactory : AbstractComponentFactory<LinearLayout>() {
    override val name: String
        get() = "LinearLayout"

    override fun buildComponent(buildContext: ComponentBuildContext): LinearLayout {
        val linearLayout = LinearLayout(buildContext.context)
        setupView(buildContext, linearLayout)

        val parent = buildContext.parents.last()
        linearLayout.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            setProperty(buildContext, parent, linearLayout, it)
        }

        return linearLayout
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: LinearLayout, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "orientation" -> setOrientation(view, property)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(LinearLayoutComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets orientations
     * 
     * @param linearLayout linear layout
     * @param pageLayoutViewProperty property to be set
     */
    private fun setOrientation(linearLayout: LinearLayout, pageLayoutViewProperty: PageLayoutViewProperty) {
        when (pageLayoutViewProperty.type) {
            PageLayoutViewPropertyType.number -> linearLayout.orientation = pageLayoutViewProperty.value.toInt()
            PageLayoutViewPropertyType.string -> when (pageLayoutViewProperty.value) {
                "horizontal" -> linearLayout.orientation = LinearLayout.HORIZONTAL
                "vertical" -> linearLayout.orientation = LinearLayout.VERTICAL
            }
            else -> linearLayout.orientation = LinearLayout.HORIZONTAL
        }
    }
}