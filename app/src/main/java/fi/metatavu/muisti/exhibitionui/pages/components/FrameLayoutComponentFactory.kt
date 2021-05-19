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
        setupView(buildContext, frameLayout)

        val parent = buildContext.parents.lastOrNull()
        frameLayout.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, frameLayout, it)
        }

        return frameLayout
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: FrameLayout, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(FrameLayoutComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

}