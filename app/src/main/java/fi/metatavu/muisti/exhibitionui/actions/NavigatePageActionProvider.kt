package fi.metatavu.muisti.exhibitionui.actions

import android.content.Intent
import android.util.Log
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Page action provider for navigate events
 *
 * Action navigates user to another page
 *
 * @constructor constructor
 * @param properties event properties
 */
class NavigatePageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    override fun performAction(pageActivity: PageActivity) {
        val pageId = getPropertyUuid("pageId")
        if (pageId != null) {
            val application = pageActivity.applicationContext as ExhibitionUIApplication
            val currentActivity = application.getCurrentActivity()

            if (currentActivity is PageActivity) {
                Log.d(javaClass.name, "Page had transition element count = " + currentActivity.transitionElements.count())
                currentActivity.goToPage(pageId, currentActivity.transitionElements)
            }
        }
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.navigate

}
