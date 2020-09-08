package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.pages.PageViewVisitorSessionListener
import fi.metatavu.muisti.exhibitionui.script.ScriptController
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import uk.co.deanwild.flowtextview.FlowTextView
import java.io.File
import java.io.FileOutputStream
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


/**
 * Abstract base class all component factories
 *
 * @param T component view class
 */
abstract class AbstractComponentFactory<T : View> : ComponentFactory<T> {

    private val context: Context = ExhibitionUIApplication.instance.applicationContext
    private val displayMetrics: DisplayMetrics = context.resources.displayMetrics

    /**
     * Sets up view
     *
     * @param buildContext build context
     * @param view view
     */
    protected fun setupView(buildContext: ComponentBuildContext, view: T) {
        setId(view, buildContext.pageLayoutView)
        initializeScriptedListener(buildContext, view)
    }

    /**
     * Sets a view component property
     *
     * @param buildContext build context
     * @param parent parent view
     * @param view view
     * @param property property to be set
     */
    protected open fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: T, property: PageLayoutViewProperty) {
        when (property.name) {
            "padding" -> setPadding(view, property)
            "paddingLeft" -> setPadding(view, property)
            "paddingTop" -> setPadding(view, property)
            "paddingRight" -> setPadding(view, property)
            "paddingBottom" -> setPadding(view, property)
            "layout_width" -> setLayoutWidth(parent, view, property)
            "layout_height" -> setLayoutHeight(parent, view, property)
            "layout_alignParentRight" -> setLayoutAlignParent(view, property)
            "layout_alignParentTop" -> setLayoutAlignParent(view, property)
            "layout_alignParentBottom" -> setLayoutAlignParent(view, property)
            "layout_alignParentEnd" -> setLayoutAlignParent(view, property)
            "layout_alignParentLeft" -> setLayoutAlignParent(view, property)
            "layout_alignParentStart" -> setLayoutAlignParent(view, property)
            "layout_marginTop" -> setLayoutMargin(view, property)
            "layout_marginBottom" -> setLayoutMargin(view, property)
            "layout_marginRight" -> setLayoutMargin(view, property)
            "layout_marginLeft" -> setLayoutMargin(view, property)
            "layout_toRightOf" -> setLayoutOf(view, property)
            "layout_gravity" -> setLayoutGravity(view, property.value)
            "gravity" -> setGravity(view, property.value)
            "background" -> setBackground(view, property.value)
            "backgroundImage" -> setBackgroundImage(buildContext, view, property.value)
            "elevation" -> setElevation(view, property)
            else -> Log.d(ImageViewComponentFactory::javaClass.name, "Property ${property.name} not supported on ${view.javaClass.name} view")
        }
    }

    /**
     * Returns color property value
     *
     * @param property property
     * @return color property value
     */
    protected fun getColor(property: PageLayoutViewProperty): Int? {
        return this.getColor(property.value)
    }

    /**
     * Returns color property value
     *
     * @param value value
     * @return color property value
     */
    protected fun getColor(value: String?): Int? {
        if (value.isNullOrBlank()) {
            return null
        }

        try {
            if (value.length === 4 && value.startsWith("#")) {
                return Color.parseColor("#" + value.substring(1).map { "$it$it" }.joinToString (""))
            } else {
                return Color.parseColor(value)
            }
        } catch (e: IllegalArgumentException) {
            return null
        }
    }


   /**
     * Returns URI property value
     *
     * @param value value
     * @return URI property value
     */
    protected fun getUri(value: String?): Uri? {
        value ?: return null
        return Uri.parse(value)
    }

    /**
     * Returns resource for given property
     *
     * @param buildContext build context
     * @param value property value
     * @return resource value for given property
     */
    protected fun getResource(buildContext: ComponentBuildContext, value: String?): ExhibitionPageResource? {
        return getResource(resources = buildContext.page.resources, value = value)
    }

    /**
     * Returns resource for given property
     *
     * @param resources resources
     * @param value property value
     * @return resource value for given property
     */
    protected fun getResource(resources: Array<ExhibitionPageResource>, value: String?): ExhibitionPageResource? {
        value ?: return null
        if (!value.startsWith("@resources/")) {
            return null
        }

        val resourceName = value.substring(11)
        return resources.firstOrNull { resourceName == it.id }
    }

    /**
     * Returns resource data for given property
     *
     * @param buildContext build context
     * @param propertyName property's name
     * @return resource value for given property
     */
    protected fun getPropertyResourceData(buildContext: ComponentBuildContext, propertyName: String): String? {
        val property = buildContext.pageLayoutView.properties.find { it.name == propertyName } ?: return null

        if (property.value.startsWith("@resources/")) {
            return getResourceData(buildContext, property)
        }

        return property.value
    }

    /**
     * Returns resource data for given property
     *
     * @param buildContext build context
     * @param propertyName property's name
     * @return resource value for given property
     */
    protected fun getPropertyResourceType(buildContext: ComponentBuildContext, propertyName: String): String? {
        val property = buildContext.pageLayoutView.properties.find { it.name == propertyName } ?: return null

        if (property.value.startsWith("@resources/")) {
            return getResourceData(buildContext, property)
        }

        return property.value
    }

    /**
     * Returns resource data for given property
     *
     * @param buildContext build context
     * @param property property
     * @return resource value for given property
     */
    protected fun getResourceData(buildContext: ComponentBuildContext, property: PageLayoutViewProperty): String? {
        return getResourceData(buildContext.page.resources, property?.value)
    }

    /**
     * Returns resource type for given property
     *
     * @param buildContext build context
     * @param property property
     * @return resource type for given property
     */
    protected fun getResourceType(buildContext: ComponentBuildContext, property: PageLayoutViewProperty): ExhibitionPageResourceType? {
        return getResourceType(buildContext.page.resources, property?.value)
    }

    /**
     * Returns resource data for given property
     *
     * @param buildContext build context
     * @param value property value
     * @return resource value for given property
     */
    protected fun getResourceData(buildContext: ComponentBuildContext, value: String?): String? {
        return getResourceData(buildContext.page.resources, value)
    }

    /**
     * Returns resource data for given property
     *
     * @param value property value
     * @return resource value for given property
     */
    protected fun getResourceData(resources: Array<ExhibitionPageResource>, value: String?): String? {
        val resource = getResource(resources, value)
        resource ?: return null
        return resource.data
    }

    /**
     * Returns resource type for given property
     *
     * @param value property value
     * @return resource type for given property
     */
    protected fun getResourceType(resources: Array<ExhibitionPageResource>, value: String?): ExhibitionPageResourceType? {
        val resource = getResource(resources, value)
        resource ?: return null
        return resource.type
    }

    /**
     * Downloads a resource into offline storage and returns a file
     *
     * @param buildContext build context
     * @param propertyName name of property containing a reference to resource
     * @return offlined file or null if not found
     */
    protected fun getResourceOfflineFile(buildContext: ComponentBuildContext, propertyName: String): File? {
        val srcValue = buildContext.pageLayoutView.properties.firstOrNull { it.name == propertyName }?.value
        return getResourceOfflineFile(buildContext.page.resources, srcValue)
    }

    /**
     * Downloads a resource into offline storage and returns a file
     *
     * @param buildContext build context
     * @param url URL of the file
     * @return offlined file or null if not found
     */
    protected fun getResourceOfflineFile(buildContext: ComponentBuildContext, url: URL?): File? {
        return getResourceOfflineFile(resources = buildContext.page.resources, url = url)
    }

    /**
     * Parses given resource as HTML
     *
     * @param buildContext build context
     * @param value property value
     * @return parsed HTML
     */
    protected fun parseHtmlResource(buildContext: ComponentBuildContext, value: String?): Spanned? {
        return parseHtml(getResourceData(buildContext, value))
    }

    /**
     * Parses given text as HTML
     *
     * @param html html text
     * @return parsed HTML
     */
    protected fun parseHtml(html: String?): Spanned? {
        html ?: return null
        if (html.isEmpty()) {
            return null
        }

        try {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } catch (e: java.lang.Exception) {
            Log.w(javaClass.name, "HTML $html parsing failed", e)
        }

        return null
    }

    /**
     * Returns scripted resource evaluated value
     *
     * @param buildContext build context
     * @param visitorSession logged visitor session
     * @param propertyName property name
     * @param returnNotScripted whether to return result of non-scripted resources
     * @return scripted resource evaluated value
     */
    protected fun getScriptedResource(buildContext: ComponentBuildContext, visitorSession: VisitorSession, propertyName: String, returnNotScripted: Boolean): String? {
        val propertyValue = buildContext.pageLayoutView.properties.firstOrNull { it.name == propertyName }?.value ?: return null
        val resource = getResource(buildContext.page.resources, propertyValue) ?: return null
        val scripted = isScriptedResource(resource)

        if (!scripted) {
            if (returnNotScripted) {
                return resource.data
            } else {
                return null
            }
        }

        return evaluateResourceScript(resource = resource, visitorSession = visitorSession)
    }

    /**
     * Returns whether resource is considered to be scripted
     *
     * @param resource resource
     * @return whether resource is considered to be scripted
     */
    private fun isScriptedResource(resource: ExhibitionPageResource?): Boolean {
        resource ?: return false
        return isScriptedResourceMode(resource.mode)
    }

    /**
     * Returns whether resource mode is considered to be scripted
     *
     * @param mode resource mode
     * @return whether resource mode is considered to be scripted
     */
    private fun isScriptedResourceMode(mode: PageResourceMode?): Boolean {
        mode ?: return false
        return when (mode) {
            PageResourceMode.dynamic -> true
            PageResourceMode.scripted -> true
            else -> false
        }
    }

    /**
     * Initializes scripted resource update listeners
     *
     * @param buildContext build context
     * @param view view
     */
    private fun initializeScriptedListener(buildContext: ComponentBuildContext, view: T) {
        buildContext.addVisitorSessionListener(object : PageViewVisitorSessionListener {

            override suspend fun prepareVisitorSessionChange(pageActivity: PageActivity, visitorSession: VisitorSession) {
                prepareBackgroundImage(visitorSession)
            }

            override fun performVisitorSessionChange(pageActivity: PageActivity, visitorSession: VisitorSession) {
                updateBackgroundImage(visitorSession)
            }

            /**
             * Prepares background image for scripted resources
             */
            private fun prepareBackgroundImage(visitorSession: VisitorSession) {
                val url = getUrl(getScriptedResource(buildContext,  visitorSession, "backgroundImage", false))
                if (url != null) {
                    getResourceOfflineFile(buildContext, url)
                }
            }

            /**
             * Updates background image for scripted resources
             */
            private fun updateBackgroundImage(visitorSession: VisitorSession) {
                val url = getUrl(getScriptedResource(buildContext, visitorSession,"backgroundImage", false))
                if (url != null) {
                    setBackgroundImage(buildContext, view, url)
                }
            }
        })
    }

    /**
     * Sets view id
     *
     * @param view view
     * @param pageLayoutView page layout view
     */
    private fun setId(view: T, pageLayoutView: PageLayoutView) {
        setId(view, pageLayoutView.id)
    }

    /**
     * Sets view id
     *
     * @param view view
     * @param value value
     */
    private fun setId(view: T, value: String?) {
        view.tag = value
        view.transitionName = value
    }

    /**
     * Evaluates scripted resource value
     *
     * @param resource scripted resource
     * @param visitorSession visitor session
     * @return Evaluated resource value
     */
    private fun evaluateResourceScript(resource: ExhibitionPageResource?, visitorSession: VisitorSession): String? {
        resource ?: return null
        val scripted = isScriptedResource(resource)
        val data = resource.data

        if (!scripted) {
            return data
        }

        val userValues = visitorSession.variables?.map { it.name to it.value }?.toMap() ?: emptyMap()
        val result = ScriptController.executeInlineFunction("function m(uv) { const userValues = {}; uv.forEach((k, v) => { userValues[k] = v }); return ${data} }", "m", arrayOf(userValues))

        return org.mozilla.javascript.Context.toString(result)
    }

    /**
     * Sets background
     *
     * @param view view
     * @param value value
     */
    private fun setBackground(view: T, value: String) {
        val color = getColor(value)
        color ?: return
        view.setBackgroundColor(color)
    }

    /**
     * Sets background image.
     *
     * On scripted resources this method is does not do anything because scripted resources are
     * handled by visitor session change events
     *
     * @param buildContext build context
     * @param view view component
     * @param value value
     */
    private fun setBackgroundImage(buildContext: ComponentBuildContext, view: View, value: String?) {
        val resource = getResource(buildContext, value)
        val scripted = isScriptedResource(resource)

        if (scripted) {
            return
        }

        val resourceData = resource?.data

        val url = getUrl(resourceData ?: value)
        url ?: return

        setBackgroundImage(buildContext = buildContext, view = view, url = url)
    }

    /**
     * Sets background image
     *
     * @param buildContext build context
     * @param view view component
     * @param url url
     */
    private fun setBackgroundImage(buildContext: ComponentBuildContext, view: View, url: URL?) {
        val offlineFile = getResourceOfflineFile(buildContext = buildContext, url = url)
        offlineFile ?: return

        val bitmap = BitmapFactory.decodeFile(offlineFile.absolutePath)
        if (bitmap != null) {
            view.background = BitmapDrawable(Resources.getSystem(), bitmap)
        }
    }

    /**
     * Sets elevation
     *
     * @param view view
     * @param property property
     */
    private fun setElevation(view: T, property: PageLayoutViewProperty) {
        val px = stringToPx(property.value)
        px ?: return
        view.elevation = px
    }

    /**
     * Sets view padding
     *
     * @param view view
     * @param property property
     */
    private fun setPadding(view: T, property: PageLayoutViewProperty) {
        val px = stringToPx(property.value)?.toInt()
        px ?: return

        when (property.name) {
            "padding" -> view.setPadding(px, px, px, px)
            "paddingLeft" -> view.setPadding(px, view.paddingTop, view.paddingRight, view.paddingBottom)
            "paddingTop" -> view.setPadding(view.paddingLeft, px, view.paddingRight, view.paddingBottom)
            "paddingRight" -> view.setPadding(view.paddingLeft, view.paddingTop, px,  view.paddingBottom)
            "paddingBottom" -> view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, px)
            else -> Log.d(ImageViewComponentFactory::javaClass.name, "Property ${property.name} not supported")
        }
    }

    /**
     * Downloads a resource into offline storage and returns a file
     *
     * @param resources resources
     * @param value value
     * @return offlined file
     */
    private fun getResourceOfflineFile(resources: Array<ExhibitionPageResource>, value: String?): File? {
        val resource = getResourceData(resources, value)
        val url = getUrl(resource ?: value)
        url ?: return null

        return getResourceOfflineFile(resources = resources, url = url)
    }

    /**
     * Downloads a resource into offline storage and returns a file
     *
     * @param resources resources
     * @param url URL of the resource
     * @return offlined file
     */
    private fun getResourceOfflineFile(resources: Array<ExhibitionPageResource>, url: URL?): File? {
        url ?: return null

        val downloadsDir = ExhibitionUIApplication.instance.applicationContext.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)

        val urlExternal = url.toExternalForm()
        val urlHash: String = md5(urlExternal)
        val extension: String = urlExternal.substring(urlExternal.lastIndexOf("."))

        val offlineFile = File(downloadsDir, "$urlHash$extension")
        if (!offlineFile.exists()) {
            Log.d(this.javaClass.name, "Downloading ${url.toExternalForm()} into local file ${offlineFile.absolutePath}")
            offlineFile.parentFile.mkdirs()
            offlineFile.createNewFile()

            try {
                val outputStream = FileOutputStream(offlineFile)
                val inputStream = url.openConnection().getInputStream()

                inputStream.use {
                    outputStream.use {
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                offlineFile.delete()
                Log.d(this.javaClass.name, "Failed to download $url", e)
            }
        }

        return offlineFile
    }

    /**
     * Returns URL property value
     *
     * @param value value
     * @return URL property value
     */
    protected fun getUrl(value: String?): URL? {
        try {
            value ?: return null
            return URL(value)
        } catch (e: MalformedURLException) {
            return null
        }
    }

    /**
     * Updates component layout gravity value
     *
     * @param view view component
     * @param value value
     */
    protected fun setLayoutGravity(view: View, value: String?) {
        val gravity = parseGravity(value)
        gravity ?: return

        if (view.layoutParams is FrameLayout.LayoutParams) {
            (view.layoutParams as FrameLayout.LayoutParams).gravity = gravity
        } else if (view.layoutParams is LinearLayout.LayoutParams) {
            (view.layoutParams as LinearLayout.LayoutParams).gravity = gravity
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${view.layoutParams.javaClass.name} for gravity")
        }
    }

    /**
     * Updates component gravity value
     *
     * @param view view component
     * @param value value
     */
    protected fun setGravity(view: View, value: String?) {
        val gravity = parseGravity(value)
        gravity ?: return
        if(view is TextView){
            view.gravity = gravity
        } else if (view is LinearLayout) {
            view.gravity = gravity
        } else if (view is RelativeLayout) {
            view.gravity = gravity
        } else if (view is Button) {
            view.gravity = gravity
        } else if (view is FlowTextView) {
            view.gravity = gravity
        }
    }

    /**
     * Updates component layout margin values
     *
     * @param view view component
     * @param property property to be set
     */
    protected fun setLayoutMargin(view: View, property: PageLayoutViewProperty) {
        val px = stringToPx(property.value)?.toInt()
        px ?: return

        val layoutParams = view.layoutParams

        if (layoutParams is MarginLayoutParams) {
            when (property.name) {
                "layout_marginTop" -> layoutParams.topMargin = px
                "layout_marginBottom" -> layoutParams.bottomMargin = px
                "layout_marginRight" -> layoutParams.rightMargin = px
                "layout_marginLeft" -> layoutParams.leftMargin = px
                else -> Log.d(this.javaClass.name, "Unsupported layout margin ${property.name} for relative layout params")
            }
        } else {
            Log.d(this.javaClass.name, "Unsupported layout params ${layoutParams.javaClass.name} for ${property.name}")
        }
    }

    /**
     * Updates component layout left|right|start|end of values
     *
     * @param view view component
     * @param property property to be set
     */
    protected fun setLayoutOf(view: View, property: PageLayoutViewProperty) {
        val value = property.value

        if (view.layoutParams is RelativeLayout.LayoutParams) {
            when (property.name) {
                "layout_toRightOf" -> (view.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.RIGHT_OF)
                "layout_toLeftOf" -> (view.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.LEFT_OF)
                "layout_toEndOf" -> (view.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.END_OF)
                "layout_toStartOf" -> (view.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.START_OF)
            }
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${view.layoutParams.javaClass.name} for layoutOf $value")
        }
    }

    /**
     * Returns initial layout params value for generated component
     *
     * @param parent parent view
     * @return initial layout params
     */
    protected fun getInitialLayoutParams(parent: View?): ViewGroup.LayoutParams {
        if (parent != null) {
            if (parent is FlowTextView) {
                return RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            } else if (parent is FrameLayout) {
                return FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            } else if (parent is LinearLayout) {
                return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            }
        }

        return ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    /**
     * Updates component layout width values
     *
     * @param parent parent component
     * @param view view component
     * @param property property to be set
     */
    @Suppress("DEPRECATION")
    protected fun setLayoutWidth(parent: View?, view: View, property: PageLayoutViewProperty) {
        if (parent == null) {
            return
        }

        val width = stringToPx(property.value)
        if (width != null) {
            view.layoutParams.width = width.toInt()
            return
        }

        if (parent is FrameLayout) {
            when (property.value) {
                "match_parent" -> view.layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
                "wrap_content" -> view.layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
                "fill_parent" -> view.layoutParams.width = FrameLayout.LayoutParams.FILL_PARENT
                else -> Log.d(this.javaClass.name, "Unsupported value ${property.value} for FrameLayout width")
            }
        } else if (parent is LinearLayout) {
            when (property.value) {
                "match_parent" -> view.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                "wrap_content" -> view.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                "fill_parent" -> view.layoutParams.width = LinearLayout.LayoutParams.FILL_PARENT
                else -> Log.d(this.javaClass.name, "Unsupported value ${property.value} for LinearLayout width")
            }
        } else if (parent is RelativeLayout) {
            when (property.value) {
                "match_parent" -> view.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                "wrap_content" -> view.layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                "fill_parent" -> view.layoutParams.width = RelativeLayout.LayoutParams.FILL_PARENT
                else -> Log.d(this.javaClass.name, "Unsupported value ${property.value} for RelativeLayout width")
            }
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for layout width")
        }
    }

    /**
     * Updates component layout align parent values
     *
     * @param view view component
     * @param property property to be set
     */
    @Suppress("DEPRECATION")
    protected fun setLayoutAlignParent(view: View, property: PageLayoutViewProperty) {
        val layoutParams = view.layoutParams

        if (property.value != "true") {
            return
        }

        if (layoutParams is RelativeLayout.LayoutParams) {
            when (property.name) {
                "layout_alignParentRight" -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                "layout_alignParentTop" -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                "layout_alignParentBottom" -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                "layout_alignParentEnd" -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                "layout_alignParentLeft" -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                "layout_alignParentStart" -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                else ->  Log.d(this.javaClass.name, "Unsupported layout align ${property.name}")
            }
        } else {
            Log.d(this.javaClass.name, "Unsupported layout params ${layoutParams.javaClass.name} for ${property.name}")
        }
    }

    /**
     * Updates component layout height values
     *
     * @param parent parent component
     * @param view view component
     * @param property property to be set
     */
    @Suppress("DEPRECATION")
    protected fun setLayoutHeight(parent: View?, view: View, property: PageLayoutViewProperty) {
        if (parent == null) {
            return
        }

        val height = stringToPx(property.value)
        if (height!= null) {
            view.layoutParams.height = height.toInt()
            return
        }

        if (parent is FrameLayout) {
            when (property.value) {
                "match_parent" -> view.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
                "wrap_content" -> view.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
                "fill_parent" -> view.layoutParams.height = FrameLayout.LayoutParams.FILL_PARENT
                else -> Log.d(this.javaClass.name, "Unsupported value ${property.value} for FrameLayout height")
            }
        } else if (parent is LinearLayout) {
            when (property.value) {
                "match_parent" -> view.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
                "wrap_content" -> view.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                "fill_parent" -> view.layoutParams.height = LinearLayout.LayoutParams.FILL_PARENT
                else -> Log.d(this.javaClass.name, "Unsupported value ${property.value} for LinearLayout height")
            }
        } else if (parent is RelativeLayout) {
            when (property.value) {
                "match_parent" -> view.layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                "wrap_content" -> view.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                "fill_parent" -> view.layoutParams.height = RelativeLayout.LayoutParams.FILL_PARENT
                else -> Log.d(this.javaClass.name, "Unsupported value ${property.value} for RelativeLayout height")
            }
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for layout height")
        }
    }

    /**
     * Parses gravity value
     *
     * @param value value
     * @return gravity value
     */
    protected fun parseGravity(value: String?): Int? {
        value ?: return null

        return value.split("|").stream()
            .map(this::parseGravityComponent)
            .reduce(0) { a: Int, b: Int -> a.or(b) }
    }

    /**
     * Parses single gravity component value
     *
     * @param value gravity component
     * @return single gravity component value
     */
    @SuppressLint("RtlHardcoded")
    private fun parseGravityComponent(value: String?): Int {
        value ?: return Gravity.NO_GRAVITY

        when (value) {
            "top" -> return Gravity.TOP
            "right" -> return Gravity.RIGHT
            "bottom" -> return Gravity.BOTTOM
            "left" -> return Gravity.LEFT
            "center" -> return Gravity.CENTER
            "center_vertical" -> return Gravity.CENTER_VERTICAL
            "center_horizontal" -> return Gravity.CENTER_HORIZONTAL
            else -> {
                Log.d(this.javaClass.name, "Could not parse gravity component ${value}")
            }
        }

        return Gravity.NO_GRAVITY
    }

    /**
     * Resolves a text align value
     *
     * @param value value
     * @return a text align or null if value could not be resolved
     */
    protected fun resolveTextAlignment(value: String?): Int? {
        value ?: return null

        when (value) {
            "inherit" -> return TEXT_ALIGNMENT_GRAVITY
            "gravity" -> return TEXT_ALIGNMENT_GRAVITY
            "text_start" -> return TEXT_ALIGNMENT_TEXT_START
            "text_end" -> return TEXT_ALIGNMENT_VIEW_END
            "center" -> return TEXT_ALIGNMENT_CENTER
            "view_start" -> return TEXT_ALIGNMENT_VIEW_START
            "view_end" -> return TEXT_ALIGNMENT_VIEW_END
            else -> Log.d(this.javaClass.name, "Unknown text align $value")
        }

        return null
    }

    /**
     * Parses string to pixels
     *
     * @param value string
     * @returns parsed number of pixels or null if string could not be parsed
     */
    protected fun stringToPx(value: String?): Float? {
        value ?: return null

        val dp = dpToFloat(value)
        if (dp != null) {
            return convertDpToPixel(dp)
        }

        val sp = spToFloat(value)
        if (sp != null) {
            return convertSpToPixel(sp)
        }

        return null
    }

    /**
     * Parses DP string into number
     *
     * @param value string
     * @returns parsed number or null if is not a DP string
     */
    private fun dpToFloat(value: String): Float? {
        return regexToFloat("([0-9.]+)dp", value)
    }

    /**
     * Parses SP string into number
     *
     * @param value string
     * @returns parsed number or null if is not a SP string
     */
    private fun spToFloat(value: String): Float? {
        return regexToFloat("([0-9.]+)sp", value)
    }

    /**
     * Parses string to float using given regex pattern
     *
     * @param regex regex
     * @param value string value
     * @return float or null if string could not be parsed
     */
    private fun regexToFloat(regex: String, value: String): Float? {
        val match = Regex(regex).find(value)
        match ?: return null
        val ( number ) = match.destructured
        return number.toFloatOrNull()
    }

    /**
     * Converts SPs to pixels.
     *
     * @param sp SPs
     * @returns pixels
     */
    private fun convertSpToPixel(sp: Float?): Float? {
        sp ?: return null
        return (sp * displayMetrics.density)
    }

    /**
     * Converts DPs to pixels
     *
     * @param dp DPs
     * @returns pixels
     */
    private fun convertDpToPixel(dp: Float?): Float? {
        dp ?: return null
        return (dp * displayMetrics.density)
    }

    /**
     * Calculates a md5 hash from given string
     *
     * @param str string
     * @return md5 hash
     */
    private fun md5(str: String): String {
        val hexString = StringBuilder()

        for (aMessageDigest in md5(str.toByteArray(StandardCharsets.UTF_8))) {
            hexString.append(Integer.toHexString(0xFF and aMessageDigest.toInt()).padStart(2, '0'))
        }

        return hexString.toString()
    }

    /**
     * Calculates a md5 hash from given bytes
     *
     * @param bytes bytes
     * @return md5 hash bytes
     */
    private fun md5(bytes: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(bytes)
        return digest.digest()
    }
}