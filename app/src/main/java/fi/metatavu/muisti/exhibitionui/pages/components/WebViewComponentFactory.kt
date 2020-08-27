package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.widget.FrameLayout
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleAdapter
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Component container Web View
 *
 * @param buildContext Component build context
 */
@SuppressLint("ViewConstructor")
class WebViewContainer(buildContext: ComponentBuildContext): FrameLayout(buildContext.context)

/**
 * Component factory for web view components
 */
class WebViewComponentFactory : AbstractComponentFactory<WebViewContainer>() {
    override val name: String
        get() = "WebView"

    override fun buildComponent(buildContext: ComponentBuildContext): WebViewContainer {
        val container = WebViewContainer(buildContext)
        setId(container, buildContext.pageLayoutView)
        val parent = buildContext.parents.lastOrNull()
        val html = readHtml(buildContext, buildContext.pageLayoutView.properties.find { it.name == "src" })

        buildContext.addLifecycleListener(object: PageViewLifecycleAdapter() {
            override fun onPageActivate(pageActivity: PageActivity) {
                val view = WebView(buildContext.context)
                if (html != null) {
                    view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }

                container.addView(view)
            }
        })

        container.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, container, it)
        }

        return container
    }

    /**
     * Reads HTML data
     *
     * @param buildContext build context
     * @param property data property
     * @return read data or null if reading fails
     */
    private fun readHtml(buildContext: ComponentBuildContext, property: PageLayoutViewProperty?): String? {
        property ?: return null
        val data = getResourceData(buildContext, property.value)

        if (data.isNullOrEmpty()) {
            return null
        }

        return data
    }

}
