package fi.metatavu.muisti.exhibitionui.actions

import android.util.Log
import android.webkit.WebView
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.exhibitionui.pages.components.WebViewContainer
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Page action provider for ending visitor session
 *
 * @constructor constructor
 * @param properties event properties
 */
class VisitorSessionEndPageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    override fun performAction(pageActivity: PageActivity) {
        //VisitorSessionContainer.endVisitorSession()
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.visitorSessionEnd

}