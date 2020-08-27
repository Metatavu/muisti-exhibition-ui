package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.material.tabs.TabLayout
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty

/**
 * Tab layout component factory
 */
class MaterialTabLayoutComponentFactory : AbstractComponentFactory<MuistiTabLayout>() {
    override val name: String
        get() = "MaterialTabLayout"

    override fun buildComponent(buildContext: ComponentBuildContext): MuistiTabLayout {
        val tabLayout = MuistiTabLayout(buildContext.context)
        setId(tabLayout, buildContext.pageLayoutView)

        val parent = buildContext.parents.last()
        tabLayout.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            setProperty(buildContext, parent, tabLayout, it)
        }

        return tabLayout
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: MuistiTabLayout, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "tabMode" -> setTabMode(view, property)
                "tabGravity" -> setTabGravity(view, property)
                "selectedTabIndicatorColor" -> setSelectedTabIndicatorColor(view, property)
                "selectedTabIndicatorGravity" -> setSelectedTabIndicatorGravity(view, property)
                "tabTextColorNormal" -> setTabTextColorNormal(view, property)
                "tabTextColorSelected" -> setTabTextColorSelected(view, property)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(MaterialTabLayoutComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets tab mode
     *
     * @param view view
     * @param property property
     */
    private fun setTabMode(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        when (property.value) {
            "scrollable" -> view.tabMode = TabLayout.MODE_SCROLLABLE
            "fixed" -> view.tabMode = TabLayout.MODE_FIXED
            else -> Log.d(this.javaClass.name, "Unknown tab mode ${property.value}")
        }

    }

    /**
     * Sets tab gravity
     *
     * @param view view
     * @param property property
     */
    private fun setTabGravity(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        when (property.value) {
            "center" -> view.tabGravity = TabLayout.GRAVITY_CENTER
            "fill" -> view.tabGravity = TabLayout.GRAVITY_FILL
            else -> Log.d(this.javaClass.name, "Unknown tab gravity ${property.value}")
        }
    }

    /**
     * Sets selected tab indicator color
     *
     * @param view view
     * @param property property
     */
    private fun setSelectedTabIndicatorColor(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        val color = getColor(property)
        if (color != null) {
            view.setSelectedTabIndicatorColor(color)
        }
    }

    /**
     * Sets selected tab indicator gravity
     *
     * @param view view
     * @param property property
     */
    private fun setSelectedTabIndicatorGravity(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        when (property.value) {
            "bottom" -> view.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_BOTTOM)
            "center" -> view.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_CENTER)
            "top" -> view.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_TOP)
            "stretch" -> view.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_STRETCH)
            else -> Log.d(this.javaClass.name, "Unknown tab gravity ${property.value}")
        }
    }

    /**
     * Sets selected tab text color normal
     *
     * @param view view
     * @param property property
     */
    private fun setTabTextColorNormal(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        val color = getColor(property)
        if (color != null) {
            view.setTabTextColorNormal(color)
        }
    }

    /**
     * Sets selected tab text color selected
     *
     * @param view view
     * @param property property
     */
    private fun setTabTextColorSelected(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        val color = getColor(property)
        if (color != null) {
            view.setTabTextColorSelected(color)
        }
    }

}

/**
 * TabLayout component that extends original class and adds better support for changing styling properties
 *
 * @constructor constructor
 *
 * @param context context
 */
class MuistiTabLayout(context: Context): TabLayout(context) {

    /**
     * Sets tab text color on normal state
     *
     * @param color color
     */
    fun setTabTextColorNormal(color: Int) {
        setTabTextColors(color, getTabTextColorSelected())
    }

    /**
     * Sets tab text color on selected state
     *
     * @param color color
     */
    fun setTabTextColorSelected(color: Int) {
        setTabTextColors(getTabTextColorNormal(), color)
    }

    /**
     * Returns tab text color on normal state
     *
     * @return color
     */
    private fun getTabTextColorNormal(): Int {
        val colors = tabTextColors ?: return Color.BLACK
        return colors.getColorForState(View.EMPTY_STATE_SET, Color.BLACK)
    }

    /**
     * Returns tab text color on selected state
     *
     * @return color
     */
    private fun getTabTextColorSelected(): Int {
        val colors = tabTextColors ?: return Color.BLACK
        return colors.getColorForState(View.SELECTED_STATE_SET, Color.BLACK)
    }

}