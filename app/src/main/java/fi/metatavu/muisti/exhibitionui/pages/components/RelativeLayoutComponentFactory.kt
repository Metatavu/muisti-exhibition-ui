package fi.metatavu.muisti.exhibitionui.pages.components

import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Component factory for relative layout components
 */
class RelativeLayoutComponentFactory : AbstractComponentFactory<RelativeLayout>() {

    override val name: String
        get() = "RelativeLayout"

    override fun buildComponent(buildContext: ComponentBuildContext): RelativeLayout {
        val frameLayout = RelativeLayout(buildContext.context)
        setId(frameLayout, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        frameLayout.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, frameLayout, it)
        }

        return frameLayout
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: RelativeLayout, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(RelativeLayoutComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }
}