package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
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


    fun insert(updateUserValueTask: UpdateUserValueTask) = viewModelScope.launch {
        updateUserValueTaskRepository.insert(updateUserValueTask)
    }

}