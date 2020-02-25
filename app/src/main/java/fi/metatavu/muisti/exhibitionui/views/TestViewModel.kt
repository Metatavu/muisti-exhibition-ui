package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import kotlinx.coroutines.launch

/**
 * View model for test activity
 *
 * @constructor
 * model constructor
 *
 * @param application application instance
 */
class TestViewModel(application: Application): AndroidViewModel(application) {

    private val updateUserValueTaskRepository: UpdateUserValueTaskRepository

    init {
        val updateUserValueTaskDao = ExhibitionUIDatabase.getDatabase().updateUserValueTaskDao()
        updateUserValueTaskRepository = UpdateUserValueTaskRepository(updateUserValueTaskDao)
    }

    suspend fun list(limit: Int): List<UpdateUserValueTask> {
        return updateUserValueTaskRepository.list(limit)
    }

    fun listLive(limit: Int): LiveData<List<UpdateUserValueTask>> {
        return updateUserValueTaskRepository.listLive(limit)
    }


    fun addUpdateUserValueTask(key: String, value: String) = viewModelScope.launch {
        val visitorSessionId = VisitorSessionContainer.getVisitorSessionId()
        if (visitorSessionId != null) {
            val updateUserValueTask = UpdateUserValueTask(
                visitorSessionId.toString(),
                System.currentTimeMillis(),
                Math.round(Math.random() * 5 - 10),
                key,
                value
            )

            updateUserValueTaskRepository.insert(updateUserValueTask)
        }
    }

}