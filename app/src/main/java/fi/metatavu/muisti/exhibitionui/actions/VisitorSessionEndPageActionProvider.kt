package fi.metatavu.muisti.exhibitionui.actions

import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
import fi.metatavu.muisti.exhibitionui.visitors.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Page action provider for ending visitor session
 *
 * @constructor constructor
 * @param properties event properties
 */
class VisitorSessionEndPageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    override fun performAction(activity: MuistiActivity) {
        VisitorSessionContainer.endVisitorSession()
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.visitorSessionEnd

}