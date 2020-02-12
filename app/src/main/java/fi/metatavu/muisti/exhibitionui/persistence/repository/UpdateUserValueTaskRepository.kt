package fi.metatavu.muisti.exhibitionui.persistence.repository

import androidx.lifecycle.LiveData
import fi.metatavu.muisti.exhibitionui.persistence.dao.UpdateUserValueTaskDao
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask

/**
 * Repository class for UpdateUserValueTasks
 *
 * @property updateUserValueTaskDao UpdateUserValueTask DAO class
 */
class UpdateUserValueTaskRepository(private val updateUserValueTaskDao: UpdateUserValueTaskDao) {

    /**
     * All tasks
     */
    val list: LiveData<List<UpdateUserValueTask>> = updateUserValueTaskDao.list()

    /**
     * Creates new task
     *
     * @param updateUserValueTask task
     */
    suspend fun insert(updateUserValueTask: UpdateUserValueTask) {
        updateUserValueTaskDao.insert(updateUserValueTask)
    }

}