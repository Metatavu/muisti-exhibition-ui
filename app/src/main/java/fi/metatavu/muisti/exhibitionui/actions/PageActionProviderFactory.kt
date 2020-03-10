package fi.metatavu.muisti.exhibitionui.actions

import android.util.Log
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty

/**
 * Page action provider factory class
 */
class PageActionProviderFactory {

    companion object {

        /**
         * Builds new page action provider
         *
         * @param action action
         * @param properties properties
         * @return constructed view or null if not found
         */
        fun buildProvider(action: ExhibitionPageEventActionType, properties: Array<ExhibitionPageEventProperty>): PageActionProvider? {
            when (action) {
                ExhibitionPageEventActionType.navigate -> return NavigatePageActionProvider(properties)
                else -> Log.d(PageActionProviderFactory::javaClass.name, "Could not page action provider for $action")
            }

            return null
        }

    }

}