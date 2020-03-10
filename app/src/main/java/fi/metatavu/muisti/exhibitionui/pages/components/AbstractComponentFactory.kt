package fi.metatavu.muisti.exhibitionui.pages.components

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
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

    /**
     * Returns color property value
     *
     * @param value value
     * @return color property value
     */
    protected fun getColor(value: String?): Int? {
        value?: return null

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
        value?: return null

        try {
            val pattern: Pattern = Pattern.compile("dp$", Pattern.MULTILINE)
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
        value?: return null
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
     * Parses gravity value
     *
     * @param value value
     * @return gravity value
     */
    private fun parseGravity(value: String?): Int {
        value?: return Gravity.NO_GRAVITY

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
    private fun parseGravityComponent(value: String?): Int {
        value?: return Gravity.NO_GRAVITY

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


}