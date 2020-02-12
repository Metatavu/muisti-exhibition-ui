package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateUserValueService : JobIntentService() {

    private val updateUserValueTaskRepository: UpdateUserValueTaskRepository

    init {
        val updateUserValueTaskDao = ExhibitionUIDatabase.getDatabase().updateUserValueTaskDao()
        updateUserValueTaskRepository = UpdateUserValueTaskRepository(updateUserValueTaskDao)
    }

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            val updateUserValueTasks = updateUserValueTaskRepository.list(1)
            if (!updateUserValueTasks.isEmpty()) {
                Log.d(javaClass.name, "DELETE ID: " + updateUserValueTasks.get(0).id)
                updateUserValueTaskRepository.delete(updateUserValueTasks.get(0))
            }
        }
    }

}