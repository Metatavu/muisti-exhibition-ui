package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
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
            try {
                val updateUserValueTasks = updateUserValueTaskRepository.list(1)
                if (updateUserValueTasks.isNotEmpty()) {
                    val updateUserValueTask = updateUserValueTasks.get(0)
                    val visitorSession = UpdateUserValue.findVisitorSession(updateUserValueTask)
                    if (visitorSession != null) {
                        UpdateUserValue.updateVisitorSessionVariable(visitorSession, updateUserValueTask.name, updateUserValueTask.value)
                        updateUserValueTaskRepository.delete(updateUserValueTask)
                    }
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, "Visitor session updating failed", e)
            }
        }
    }
}

object UpdateUserValue {
    /**
     * Updates a visitor session variable into the API
     *
     * @param visitorSession visitor session
     * @param key key
     * @param value value
     */
    suspend fun updateVisitorSessionVariable(visitorSession: VisitorSessionV2, key: String, value: String) {
        try {
            val variables = (visitorSession.variables ?: arrayOf()).filter { !it.name.equals(key) }.toTypedArray()
            val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()

            visitorSessionsApi.updateVisitorSessionV2(
                visitorSession.exhibitionId!!,
                visitorSession.id!!,
                visitorSession.copy(variables = variables.plus(VisitorSessionVariable(key, value)))
            )
        } catch (e: Exception) {
            Log.e(javaClass.name, "Failed to update visitor session variable", e)
        }
    }

    /**
     * Finds a visitor session for a task
     *
     * @param updateUserValueTask task
     * @return a visitor session for a task
     */
    suspend fun findVisitorSession(updateUserValueTask: UpdateUserValueTask1): VisitorSessionV2? {
        try {
            val exhibitionId = DeviceSettings.getExhibitionId()
            val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()
            val visitorSessionId = updateUserValueTask.sessionId

            if (exhibitionId == null) {
                return null
            }

            return visitorSessionsApi.findVisitorSessionV2(exhibitionId, visitorSessionId)
        } catch (e: Exception) {
            Log.e(javaClass.name, "Failed to retrieve visitor session", e)
        }

        return null
    }
}