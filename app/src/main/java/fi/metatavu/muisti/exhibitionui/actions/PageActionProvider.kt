package fi.metatavu.muisti.exhibitionui.actions

import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Interface that describes a single page action provider
 */
interface PageActionProvider {

    /**
     * Performs an action
     *
     * @param pageActivity page activity where the action will be performed
     */
    fun performAction(pageActivity: PageActivity)

    /**
     * Action type
     */
    val action: ExhibitionPageEventActionType

}