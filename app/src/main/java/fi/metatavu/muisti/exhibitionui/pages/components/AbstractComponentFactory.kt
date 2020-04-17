package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
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
     * Sets view id
     *
     * @param view view
     * @param pageLayoutView page layout view
     */
    protected fun setId(view: T, pageLayoutView: PageLayoutView) {
        setId(view, pageLayoutView.id)
    }

    /**
     * Sets view id
     *
     * @param view view
     * @param value value
     */
    protected fun setId(view: T, value: String?) {
        view.tag = value
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
            "background" -> setBackground(view, property.value)
            else -> Log.d(ImageViewComponentFactory::javaClass.name, "Property ${property.name} not supported on ${view.javaClass.name} view")
        }
    }

    /**
     * Returns color property value
     *
     * @param value value
     * @return color property value
     */
    protected fun getColor(value: String?): Int? {
        value ?: return null

        try {
            return Color.parseColor(value)
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
        return getResource(resources, value)?.data
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

        val downloadsDir = ExhibitionUIApplication.instance.applicationContext.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)

        val urlExternal = url.toExternalForm()
        val urlHash: String = md5(urlExternal)
        val extension: String = urlExternal.substring(urlExternal.lastIndexOf("."))

        val offlineFile = File(downloadsDir, "$urlHash$extension")
        if (!offlineFile.exists()) {
            Log.d(this.javaClass.name, "Downloading ${url.toExternalForm()} into local file ${offlineFile.absolutePath}")
            offlineFile.createNewFile()

            val outputStream  = FileOutputStream(offlineFile)
            val inputStream = url.openConnection().getInputStream()

            inputStream.use{
                outputStream.use {
                    inputStream.copyTo(outputStream)
                }
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