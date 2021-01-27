package fi.metatavu.muisti.exhibitionui.actions

import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
import fi.metatavu.muisti.exhibitionui.visitors.VisibleVisitorsContainer
import fi.metatavu.muisti.exhibitionui.visitors.VisitorSessionContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Page action provider for starting visitor session
 *
 * @constructor constructor
 * @param properties event properties
 */
class StartVisitorSessionPageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    override fun performAction(activity: MuistiActivity) {
        val visitors = VisibleVisitorsContainer.getLiveVisibleVisitors().value ?: return

        GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId()
            if (exhibitionId != null) {
                val language = getPropertyString("language") ?: "FI"
                val startSession = getPropertyString("start-session") ?: "true"
                val visitorIds = visitors.mapNotNull(Visitor::id).toTypedArray()
                val visitorTags = visitors.mapNotNull(Visitor::tagId)

                val visitorSession = MuistiApiFactory.getVisitorSessionsApi().createVisitorSession(
                    exhibitionId = exhibitionId,
                    visitorSession = VisitorSession(
                        state = VisitorSessionState.aCTIVE,
                        language = language,
                        visitorIds = visitorIds
                    )
                )

                if (startSession.toBoolean()) {
                    VisitorSessionContainer.startVisitorSession(
                            visitorSession = visitorSession,
                            visitorSessionTags = visitorTags
                    )
                }
            }
        }
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.startVisitorSession

}