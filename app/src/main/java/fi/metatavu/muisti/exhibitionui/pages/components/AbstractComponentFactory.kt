package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.PageLayoutViewPropertyType
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


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
     * @param value value
     */
    protected fun setId(view: T, value: String?) {
        view.tag = value
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
     * Returns dps property value
     *
     * @param value value
     * @return dps property value
     */
    protected fun getDps(value: String?): Int? {
        value ?: return 0

        try {
            val pattern: Pattern = Pattern.compile("dp$", Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(value)
            val result: String = matcher.replaceAll("")

            return convertDpToPixel(result.toDouble()).toInt()

        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    /**
     * Returns sp property value
     *
     * @param value value
     * @return dps property value
     */
    protected fun getSp(value: String?): Float? {
        value ?: return null

        try {
            val pattern: Pattern = Pattern.compile("sp$", Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(value)
            val result: String = matcher.replaceAll("")
            return result.toFloat()
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
     * Returns resource value for given property
     *
     * @param value property value
     * @return resource value for given property
     */
    protected fun getResource(resources: Array<ExhibitionPageResource>, value: String?): String? {
        value ?: return null
        if (!value.startsWith("@resources/")) {
            return null
        }

        val resourceName = value.substring(11)
        val resource = resources.firstOrNull { resourceName == it.id }
        return resource?.data
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
     * @param parent parent component
     * @param view view component
     * @param property property to be set
     */
    protected fun setLayoutMargin(parent: View?, view: View, property: PageLayoutViewProperty) {
        val dps = getDps(property.value)
        dps ?: return

        if (parent == null) {
            Log.d(this.javaClass.name, "Parent is null, could not set layout margin")
        } else if (parent is FrameLayout) {
            when (property.name) {
                "layout_marginTop" -> (view.layoutParams as FrameLayout.LayoutParams).topMargin = dps
                "layout_marginBottom" -> (view.layoutParams as FrameLayout.LayoutParams).bottomMargin = dps
                "layout_marginRight" -> (view.layoutParams as FrameLayout.LayoutParams).rightMargin = dps
                "layout_marginLeft" -> (view.layoutParams as FrameLayout.LayoutParams).leftMargin = dps
            }
        } else if (parent is LinearLayout) {
            when(property.name){
                "layout_marginTop" -> (view.layoutParams as LinearLayout.LayoutParams).topMargin = dps
                "layout_marginBottom" -> (view.layoutParams as LinearLayout.LayoutParams).bottomMargin = dps
                "layout_marginRight" -> (view.layoutParams as LinearLayout.LayoutParams).rightMargin = dps
                "layout_marginLeft" -> (view.layoutParams as LinearLayout.LayoutParams).leftMargin = dps
            }
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for gravity")
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
            if (parent is FrameLayout) {
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

        val width = getDps(property.value)
        if (width != null) {
            view.layoutParams.width = width
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

        val height = getDps(property.value)
        if (height!= null) {
            view.layoutParams.height = height
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
     * Converts dps into pixels
     *
     * @param dp dp
     * @return pixels
     */
    private fun convertDpToPixel(dp: Double): Double {
        return dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

}