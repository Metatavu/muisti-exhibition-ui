package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleAdapter
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Tab layout component factory
 */
class MaterialTabLayoutComponentFactory : AbstractComponentFactory<MuistiTabLayout>() {
    override val name: String
        get() = "MaterialTabLayout"

    override fun buildComponent(buildContext: ComponentBuildContext): MuistiTabLayout {
        val tabLayout = MuistiTabLayout(buildContext.context)
        val pageLayoutView = buildContext.pageLayoutView

        setId(tabLayout, pageLayoutView)

        val parent = buildContext.parents.last()
        tabLayout.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            setProperty(buildContext, parent, tabLayout, it)
        }

        initializeTabs(buildContext = buildContext, view = tabLayout, parent = parent)

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
                "unboundedRipple" -> setUnboundedRipple(view, property)
                "tabIndicatorFullWidth" -> setTabIndicatorFullWidth(view, property)
                "data" -> {}
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

    /**
     * Sets unbounded ripple
     *
     * @param view view
     * @param property property
     */
    private fun setUnboundedRipple(view: MuistiTabLayout, property: PageLayoutViewProperty) {
       view.setUnboundedRipple(property.value == "true")
    }

    /**
     * Sets tab indicator full width
     *
     * @param view view
     * @param property property
     */
    private fun setTabIndicatorFullWidth(view: MuistiTabLayout, property: PageLayoutViewProperty) {
        view.isTabIndicatorFullWidth = property.value == "true"
    }

    /**
     * Reads tab data
     *
     * @param buildContext build context
     * @param property data property
     * @return read data or null if reading fails
     */
    private fun readData(buildContext: ComponentBuildContext, property: PageLayoutViewProperty?): TabData? {
        property ?: return null
        val data = getResourceData(buildContext, property.value)

        if (data.isNullOrEmpty()) {
            return null
        }

        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi.adapter(TabData::class.java)

        return jsonAdapter.fromJson(data)
    }

    /**
     * Initializes tabs
     *
     * @param buildContext build context
     * @param parent view parent
     * @param view view
     */
    private fun initializeTabs(buildContext: ComponentBuildContext, parent: View, view: MuistiTabLayout) {
        val data = readData(buildContext, buildContext.pageLayoutView.properties.find { it.name == "data" })

        if (data != null) {
            val tabContentComponents = constructTabContentComponents(
                buildContext = buildContext,
                data = data,
                view = view
            )

            addTabChangeListener(
                view = view,
                tabContentComponents = tabContentComponents
            )

            initializeTabContentComponents(
                buildContext = buildContext,
                parent = parent,
                data = data,
                tabContentComponents = tabContentComponents
            )
        }

    }

    /**
     * Adds listener for tab changes
     *
     * @param view view
     * @param tabContentComponents tab content components
     */
    private fun addTabChangeListener(view: MuistiTabLayout, tabContentComponents: List<View>) {
        view.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                tabContentComponents.forEachIndexed { index, tabContentComponent ->
                    run {
                        if (index == position) {
                            tabContentComponent.visibility = View.VISIBLE
                        } else {
                            tabContentComponent.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    /**
     * Constructs tab content components
     *
     * @param buildContext build context
     * @param view view
     * @param data tabs data
     * @return tab content components
     */
    private fun constructTabContentComponents(buildContext: ComponentBuildContext, view: MuistiTabLayout, data: TabData): List<View> {
        val pageLayoutView = buildContext.pageLayoutView

        return data.tabs.mapIndexed { index, tabData ->
            run {
                val tab = view.newTab()
                tab.text = tabData.label
                view.addTab(tab)

                val tabContentComponent = MediaViewComponentFactory().buildComponent(
                    ComponentBuildContext(
                        context = buildContext.context,
                        parents = buildContext.parents.plus(view),
                        pageLayoutView = PageLayoutView(
                            id = "${pageLayoutView.id}/$index",
                            children = emptyArray(),
                            properties = tabData.properties,
                            name = "${pageLayoutView.name}/$index",
                            widget = PageLayoutWidgetType.mediaView
                        ),
                        page = buildContext.page.copy(
                            resources = tabData.resources
                        ),
                        lifecycleListeners = buildContext.lifecycleListeners,
                        visitorSessionListeners = buildContext.visitorSessionListeners
                    )
                )

                tabContentComponent
            }
        }
    }

    /**
     * Initializes tab content components
     *
     * @param buildContext build context
     * @param parent view parent
     * @param data tabs data
     * @param tabContentComponents tab content components
     */
    private fun initializeTabContentComponents(buildContext: ComponentBuildContext, parent: View, data: TabData, tabContentComponents: List<View>) {
        buildContext.addLifecycleListener(object: PageViewLifecycleAdapter() {
            override fun onPageActivate(pageActivity: PageActivity) {
                val target = parent.findViewWithTag<ViewGroup>(data.contentContainerId)
                if (target != null) {
                    tabContentComponents.forEach {
                        target.addView(it)
                        it.visibility = View.GONE
                    }

                    tabContentComponents.first().visibility = View.VISIBLE
                }
            }
        })
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

/**
 * Moshi data class for reading serialized tab data
 *
 * @property contentContainerId content container layout element id
 * @property tabs tab data items
 */
data class TabData (

    val contentContainerId: String,
    val tabs: Array<TabDataTab>

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TabData

        if (contentContainerId != other.contentContainerId) return false
        if (!tabs.contentEquals(other.tabs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentContainerId.hashCode()
        result = 31 * result + tabs.contentHashCode()
        return result
    }
}

/**
 * Moshi data class for reading serialized tab item data
 *
 * @property label tab label
 * @property properties tab properties
 * @property resources tab resources
 */
data class TabDataTab (
    val label: String,
    val properties: Array<PageLayoutViewProperty>,
    val resources: Array<ExhibitionPageResource>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TabDataTab

        if (label != other.label) return false
        if (!properties.contentEquals(other.properties)) return false
        if (!resources.contentEquals(other.resources)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + properties.contentHashCode()
        result = 31 * result + resources.contentHashCode()
        return result
    }
}