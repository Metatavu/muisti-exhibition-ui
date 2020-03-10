package fi.metatavu.muisti.exhibitionui.pages

import android.view.View
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventTrigger

/**
 * Data class for storing generated page views
 *
 * @property view view
 * @property eventTriggers event triggers
 */
data class PageView (

    val view: View,

    val eventTriggers: Array<ExhibitionPageEventTrigger>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageView

        if (view != other.view) return false
        if (!eventTriggers.contentEquals(other.eventTriggers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = view.hashCode()
        result = 31 * result + eventTriggers.contentHashCode()
        return result
    }
}