package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.pages.PageViewActivator

/**
 * Component factory for frame layout components
 */
class FrameLayoutComponentFactory : AbstractComponentFactory<FrameLayout>() {

    override val name: String
        get() = "FrameLayout"

    override fun buildComponent(context: Context, parents: Array<View>, pageLayoutView: PageLayoutView, resources: Array<ExhibitionPageResource>, activators: MutableList<PageViewActivator>): FrameLayout {
        val frameLayout = FrameLayout(context)
        setId(frameLayout, pageLayoutView)

        val parent = parents.lastOrNull()
        frameLayout.layoutParams = getInitialLayoutParams(parent)

        pageLayoutView.properties.forEach {
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