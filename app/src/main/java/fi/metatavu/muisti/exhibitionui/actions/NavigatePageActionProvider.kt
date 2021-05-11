package fi.metatavu.muisti.exhibitionui.actions

import android.content.Intent
import android.util.Log
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
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

    override fun performAction(activity: MuistiActivity) {
        val pageId = getPropertyUuid("pageId")
        if (pageId != null) {
            if (PageViewContainer.getPageView(pageId) == null) {
                Log.d(this.javaClass.name, "Tried to navigate to non-existing page $pageId")
                return
            }

            val application = activity.applicationContext as ExhibitionUIApplication
            val currentActivity = application.getCurrentActivity()

            if (currentActivity is PageActivity) {
                if (currentActivity.pageId != pageId) {
                    currentActivity.goToPage(pageId, currentActivity.transitionElements)
                } else {
                    Log.d(this.javaClass.name, "Tried to navigate to same page $pageId again.")
                }
            }
        } else {
            Log.d(this.javaClass.name, "Tried to navigate to null page")
        }
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.navigate

}
