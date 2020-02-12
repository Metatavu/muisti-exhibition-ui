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
        val updateUserValueTaskDao = ExhibitionUIDatabase.getDatabase(application).updateUserValueTaskDao()
        updateUserValueTaskRepository = UpdateUserValueTaskRepository(updateUserValueTaskDao)
    }

    fun list(): LiveData<List<UpdateUserValueTask>> {
        return updateUserValueTaskRepository.list
    }

    fun insert(updateUserValueTask: UpdateUserValueTask) = viewModelScope.launch {
        updateUserValueTaskRepository.insert(updateUserValueTask)
    }

}