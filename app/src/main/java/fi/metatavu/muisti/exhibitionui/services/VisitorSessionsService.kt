package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSessionState
import fi.metatavu.muisti.api.client.models.VisitorSessionV2
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.visitors.ExhibitionVisitorsContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Service for caching visitor sessions.
 */
class VisitorSessionsService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        updateVisitorSessions()
    }

    /**
     * Updates the visitor sessions list from the API
     */
    private fun updateVisitorSessions() = GlobalScope.launch {
        ExhibitionVisitorsContainer.setVisitorSessions(ADMIN_OVERRIDES)
        ExhibitionVisitorsContainer.setVisitors(ADMIN_OVERRIDES_VISITORS)
    }

    companion object {
        var VISITOR_LIST_MODIFIED_AFTER: OffsetDateTime? = null
        val ADMIN_OVERRIDES_VISITORS = arrayOf(Visitor(
            id = UUID.randomUUID(),
            language = "FI",
            email = "admin@example.com",
            tagId = BuildConfig.KEYCLOAK_DEMO_TAG
        ))

        val ADMIN_OVERRIDES = listOf(VisitorSessionV2(
            tags = arrayOf(BuildConfig.KEYCLOAK_DEMO_TAG),
            id = UUID.randomUUID(),
            visitorIds = ADMIN_OVERRIDES_VISITORS.map { it.id!! }.toTypedArray(),
            language = "FI",
            state = VisitorSessionState.aCTIVE
        ))
    }

}