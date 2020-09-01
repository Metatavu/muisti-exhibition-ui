package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.FrameLayout
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleAdapter
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import fi.metatavu.muisti.api.client.models.ExhibitionPageResourceType

/**
 * Component container Web View
 *
 * @param buildContext Component build context
 */
@SuppressLint("ViewConstructor")
class WebViewContainer(buildContext: ComponentBuildContext, val data: String?, val contentType: String): FrameLayout(buildContext.context) {

    private var webView: WebView? = null

    init {
        buildContext.addLifecycleListener(object: PageViewLifecycleAdapter() {
            @SuppressLint("SetJavaScriptEnabled")
            override fun onPageActivate(pageActivity: PageActivity) {
                val view = WebView(buildContext.context)
                if (data != null) {
                    // val contentType = getContentType(type)
                    view.loadDataWithBaseURL(null, data, contentType, "UTF-8", null)
                    view.settings.javaScriptEnabled = true
                }

                addView(view)

                webView = view
            }
        })
    }

    /**
     * Evaluates Javascript in web view
     *
     * @param script script
     * @param resultCallback result callback
     */
    fun evaluateJavascript(script: String?, resultCallback: ValueCallback<String>?) {
        webView?.evaluateJavascript(script, resultCallback)
    }

}

/**
 * Component factory for web view components
 */
class WebViewComponentFactory : AbstractComponentFactory<WebViewContainer>() {
    override val name: String
        get() = "WebView"

    override fun buildComponent(buildContext: ComponentBuildContext): WebViewContainer {
        val srcProperty = buildContext.pageLayoutView.properties.firstOrNull { it.name == "src" }
        val data = readData(buildContext = buildContext, property = srcProperty)
        val type = readType(buildContext = buildContext, property = srcProperty)
        val contentType = getContentType(resourceType = type)

        val container = WebViewContainer(buildContext = buildContext, data = data, contentType = contentType)
        setupView(buildContext, container)
        val parent = buildContext.parents.lastOrNull()

        container.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, container, it)
        }

        return container
    }

    /**
     * Reads view data
     *
     * @param buildContext build context
     * @param property data property
     * @return read data or null if reading fails
     */
    private fun readData(buildContext: ComponentBuildContext, property: PageLayoutViewProperty?): String? {
        property ?: return null
        val data = getResourceData(buildContext, property.value)

        if (data.isNullOrEmpty()) {
            return null
        }

        return data
    }

    /**
     * Reads view data type
     *
     * @param buildContext build context
     * @param property data property
     * @return read data type or null if reading fails
     */
    private fun readType(buildContext: ComponentBuildContext, property: PageLayoutViewProperty?): ExhibitionPageResourceType? {
        property ?: return null
        return getResourceType(buildContext, property)
    }

    /**
     * Returns content type for given resource type
     *
     * @param resourceType resource type
     * @return content type
     */
    private fun getContentType(resourceType: ExhibitionPageResourceType?): String {
        if (resourceType == ExhibitionPageResourceType.svg) {
            return "image/svg+xml"
        }

        return "text/html"
    }

}
