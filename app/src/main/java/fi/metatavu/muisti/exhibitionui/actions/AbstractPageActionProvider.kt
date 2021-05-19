package fi.metatavu.muisti.exhibitionui.actions

import android.util.Log
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import java.util.*

/**
 * Abstract base class page page action providers
 *
 * @property properties event properties
 */
abstract class AbstractPageActionProvider(private val properties: Array<ExhibitionPageEventProperty>): PageActionProvider {

    /**
     * Returns property as UUID
     *
     * @param name property name
     * @return property as UUID or null if not found
     */
    protected fun getPropertyUuid(name: String): UUID? {
        val value = getPropertyString(name)
        value ?: return null

        if (value.isEmpty()) {
            return null
        }

        try {
            return UUID.fromString(value)
        } catch (e: IllegalArgumentException) {
            Log.e(this.javaClass.name, "Invalid UUID ($value) as property $name value")
            return null
        }
    }

    /**
     * Returns property as string
     *
     * @param name property name
     * @return property as string or null if not found
     */
    protected fun getPropertyString(name: String): String? {
        return getProperty(name)?.value
    }

    /**
     * Returns property by name
     *
     * @param name property name
     * @return property or null if not found
     */
    protected fun getProperty(name: String): ExhibitionPageEventProperty? {
        return this.properties.firstOrNull { it.name == name }
    }

}