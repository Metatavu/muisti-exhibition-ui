package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.api.client.models.VisitorSessionVariable
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask as UpdateUserValueTask1

/**
 * Service for synchronizing user values into the API
 */
class UpdateUserValueService : JobIntentService() {

    private val updateUserValueTaskRepository: UpdateUserValueTaskRepository

    /**
     * Constructor
     */
    init {
        val updateUserValueTaskDao = ExhibitionUIDatabase.getDatabase().updateUserValueTaskDao()
        updateUserValueTaskRepository = UpdateUserValueTaskRepository(updateUserValueTaskDao)
    }

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            val updateUserValueTasks = updateUserValueTaskRepository.list(1)
            if (!updateUserValueTasks.isEmpty()) {
                val updateUserValueTask = updateUserValueTasks.get(0)
                val visitorSession = findVisitorSession(updateUserValueTask)

                if (visitorSession != null) {
                    updateVisitorSessionVariable(visitorSession, updateUserValueTask.key, updateUserValueTask.value)
                    updateUserValueTaskRepository.delete(updateUserValueTask)
                }
            }
        }
    }

    /**
     * Updates a visitor session variable into the API
     *
     * @param visitorSession visitor session
     * @param key key
     * @param value value
     */
    private suspend fun updateVisitorSessionVariable(visitorSession: VisitorSession, key: String, value: String) {
        var variables = visitorSession.variables
        if (variables == null) {
            variables = arrayOf()
        }

        variables = variables.filter { !it.name.equals(key) }.toTypedArray()
        val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()

        visitorSessionsApi.updateVisitorSession(visitorSession.exhibitionId!!, visitorSession.id!!, visitorSession.copy(variables = variables.plus(VisitorSessionVariable(key, value))))
    }

    /**
     * Finds a visitor session for a task
     *
     * @param updateUserValueTask task
     * @return a visitor session for a task
     */
    private suspend fun findVisitorSession(updateUserValueTask: UpdateUserValueTask1): VisitorSession? {
        val exhibitionId = DeviceSettings.getExhibitionId()
        val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()
        val visitorSessionId = UUID.fromString(updateUserValueTask.sessionId)

        if (exhibitionId == null) {
            return null
        }

        return visitorSessionsApi.findVisitorSession(exhibitionId, visitorSessionId)
    }

}