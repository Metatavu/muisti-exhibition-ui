package fi.metatavu.muisti.exhibitionui.pages.components

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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
    protected fun getDps(value: String?): Int {
        value ?: return 0

        try {
            val pattern: Pattern = Pattern.compile("dp$", Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(value)
            val result: String = matcher.replaceAll("")
            return convertDpToPixel(result.toDouble()).toInt()

        } catch (e: IllegalArgumentException) {
            return 0
        }
    }

    /**
     * Returns sp property value
     *
     * @param value value
     * @return dps property value
     */
    protected fun getSp(value: String?): Int? {
        value ?: return null

        try {
            val pattern: Pattern = Pattern.compile("sp$", Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(value)
            val result: String = matcher.replaceAll("")
            return result.toInt()
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
     * @param parent parent component
     * @param view view component
     * @param value value
     */
    protected fun setLayoutGravity(parent: View, view: View, value: String?) {
        if (parent is FrameLayout) {
            val layoutParams = view.layoutParams
            val updatedParams = FrameLayout.LayoutParams(layoutParams.width, layoutParams.height)
            updatedParams.gravity = parseGravity(value)
            view.layoutParams = updatedParams
        } else if (parent is LinearLayout) {
            val layoutParams = view.layoutParams
            val updatedParams = LinearLayout.LayoutParams(layoutParams.width, layoutParams.height)
            updatedParams.gravity = parseGravity(value)
            view.layoutParams = updatedParams
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for gravity")
        }
    }

    /**
     * Updates component layout margin values
     *
     * @param parent parent component
     * @param view view component
     * @param property property to be set
     */
    protected fun setLayoutMargin(parent: View, view: View, property: PageLayoutViewProperty) {

        if(view.layoutParams is ViewGroup.MarginLayoutParams && view !is ImageView){
            val updatedParams = (view.layoutParams as? ViewGroup.MarginLayoutParams)
            when(property.name){
                "layout_marginTop" -> updatedParams?.topMargin = getDps(property.value)
                "layout_marginBottom" -> updatedParams?.bottomMargin = getDps(property.value)
                "layout_marginRight" -> updatedParams?.rightMargin = getDps(property.value)
                "layout_marginLeft" -> updatedParams?.leftMargin = getDps(property.value)
            }
            view.layoutParams = updatedParams
        } else if (parent is FrameLayout) {
            val layoutParams = view.layoutParams
            val updatedParams = FrameLayout.LayoutParams(layoutParams.width, layoutParams.height)
            when(property.name){
                "layout_marginTop" -> updatedParams.topMargin = getDps(property.value)
                "layout_marginBottom" -> updatedParams.bottomMargin = getDps(property.value)
                "layout_marginRight" -> updatedParams.rightMargin = getDps(property.value)
                "layout_marginLeft" -> updatedParams.leftMargin = getDps(property.value)
            }
            view.layoutParams = updatedParams
        } else if (parent is LinearLayout) {
            val layoutParams = view.layoutParams
            val updatedParams = LinearLayout.LayoutParams(layoutParams.width, layoutParams.height)
            when(property.name){
                "layout_marginTop" -> updatedParams.topMargin = getDps(property.value)
                "layout_marginBottom" -> updatedParams.bottomMargin = getDps(property.value)
                "layout_marginRight" -> updatedParams.rightMargin = getDps(property.value)
                "layout_marginLeft" -> updatedParams.leftMargin = getDps(property.value)
            }
            view.layoutParams = updatedParams
        } else {
            Log.d(this.javaClass.name, "Unsupported layout ${parent.javaClass.name} for gravity")
        }
    }

    /**
     * Sets layout widths
     *
     * @param layout any layout
     * @param property width property to be set
     */
    protected fun  setLayoutWidths(layout: View, property: PageLayoutViewProperty) {
        when (property.type) {
            PageLayoutViewPropertyType.number -> layout.layoutParams.width = property.value.toInt()
            PageLayoutViewPropertyType.string -> when (property.value) {
                "match_parent" -> layout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                "wrap_content" -> layout.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                "fill_parent" -> layout.layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT
                else -> {
                    layout.layoutParams.width = getDps(property.value)
                }
            }
        }
    }

    /**
     * Sets layout heights
     *
     * @param layout any layout
     * @param property height property to be set
     */
    protected fun setLayoutHeights(layout: View, property: PageLayoutViewProperty) {
        when (property.type) {
            PageLayoutViewPropertyType.number -> layout.layoutParams.height = property.value.toInt()
            PageLayoutViewPropertyType.string -> when (property.value) {
                "match_parent" -> layout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                "wrap_content" -> layout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                else -> {
                    layout.layoutParams.width = getDps(property.value)
                }
            }

        }
    }

    /**
     * Parses gravity value
     *
     * @param value value
     * @return gravity value
     */
    protected fun parseGravity(value: String?): Int {
        value ?: return Gravity.NO_GRAVITY

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
    protected fun parseGravityComponent(value: String?): Int {
        value ?: return Gravity.NO_GRAVITY

        when (value) {
            "top" -> return Gravity.TOP
            "right" -> return Gravity.RIGHT
            "bottom" -> return Gravity.BOTTOM
            "left" -> return Gravity.LEFT
            else -> {
                Log.d(this.javaClass.name, "Could not parse gravity component ${value}")
            }
        }

        return Gravity.NO_GRAVITY
    }

    protected fun resolveTextAlign(layout: View, property: String) {
        var value: String = property.toLowerCase();
        when (value) {
            "inherit" -> layout.textAlignment = TEXT_ALIGNMENT_GRAVITY
            "gravity" -> layout.textAlignment = TEXT_ALIGNMENT_GRAVITY
            "text_start" -> layout.textAlignment = TEXT_ALIGNMENT_TEXT_START
            "text_end" -> layout.textAlignment = TEXT_ALIGNMENT_VIEW_END
            "center" -> layout.textAlignment = TEXT_ALIGNMENT_CENTER
            "view_start" -> layout.textAlignment = TEXT_ALIGNMENT_VIEW_START
            "view_end" -> layout.textAlignment = TEXT_ALIGNMENT_VIEW_END
            else -> layout.textAlignment = TEXT_ALIGNMENT_GRAVITY
        }
    }

    protected fun resolveTextSize(layout: View, property: String) {
        if(layout is TextView){
            val value = getSp(property)
            layout.textSize = value!!.toFloat()
        }
    }


    fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            displayMetrics
        )
    }
    fun dpToSp(dp: Double): Double {
        return (convertDpToPixel(
            dp
        ) / displayMetrics.scaledDensity)
    }

    fun convertDpToPixel(dp: Double): Double {
        return dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Double): Double {
        return px / (displayMetrics.densityDpi  / DisplayMetrics.DENSITY_DEFAULT)
    }
    }