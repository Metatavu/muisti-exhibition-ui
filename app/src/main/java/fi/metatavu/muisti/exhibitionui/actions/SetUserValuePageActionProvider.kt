package fi.metatavu.muisti.exhibitionui.actions

import android.util.Log
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import java.util.*

/**
 * Page action provider for set user value
 *
 * Action saves a user value
 *
 * @constructor constructor
 * @param properties event properties
 */
class SetUserValuePageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    private val updateUserValueTaskRepository: UpdateUserValueTaskRepository = UpdateUserValueTaskRepository(ExhibitionUIDatabase.getDatabase().updateUserValueTaskDao())

    override fun performAction(pageActivity: PageActivity) {
        val name = getPropertyString("name")
        if (name == null) {
            Log.d( javaClass.name,"Name required for set user value action")
            return
        }

        val value = getPropertyString("value")
        if (value == null) {
            Log.d( javaClass.name,"Value is required for set user value action")
            return
        }

        val sessionId = VisitorSessionContainer.getVisitorSessionId()
        if (sessionId == null) {
            Log.d( javaClass.name,"Visitor sessionId is required for set user value action")
            return
        }

        val time = System.currentTimeMillis()
        val priority = 0L

        VisitorSessionContainer.setVisitorSessionUserVariable(name = name, value = value)
        pageActivity.triggerVisitorSessionChange()

        updateUserValueTaskRepository.insert(UpdateUserValueTask(
            sessionId = sessionId,
            time = time,
            priority = priority,
            name = name,
            value = value
        ))
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.setuservalue

}