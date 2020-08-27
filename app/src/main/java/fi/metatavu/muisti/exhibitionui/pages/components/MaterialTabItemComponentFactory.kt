package fi.metatavu.muisti.exhibitionui.pages.components

import android.util.Log
import android.view.View
import com.google.android.material.tabs.TabItem
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import java.lang.reflect.Modifier

/**
 * Tab item component factory
 */
class MaterialTabItemComponentFactory : AbstractComponentFactory<TabItem>() {
    override val name: String
        get() = "MaterialTabItem"

    override fun buildComponent(buildContext: ComponentBuildContext): TabItem {
        val tabItem = TabItem(buildContext.context)
        setId(tabItem, buildContext.pageLayoutView)

        val parent = buildContext.parents.last()
        tabItem.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            setProperty(buildContext, parent, tabItem, it)
        }

        return tabItem
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: TabItem, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "text" -> setText(buildContext, view, property)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(MaterialTabItemComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets view text
     *
     * @param buildContext build context
     * @param view view component
     * @param property property
     */
    private fun setText(buildContext: ComponentBuildContext, view: TabItem, property: PageLayoutViewProperty) {
        val textField = view.javaClass.declaredFields.find {
            it.name == "text"
        }

        if (textField != null) {
            val text = getResourceData(buildContext, property.value)
            textField.isAccessible = true
            textField.set(view, text)
        }
    }

}