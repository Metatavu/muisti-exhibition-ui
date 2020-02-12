package fi.metatavu.muisti.exhibitionui.persistence.repository

import androidx.lifecycle.LiveData
import androidx.room.Delete
import fi.metatavu.muisti.exhibitionui.persistence.dao.UpdateUserValueTaskDao
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask

/**
 * Repository class for UpdateUserValueTasks
 *
 * @property updateUserValueTaskDao UpdateUserValueTask DAO class
 */
class UpdateUserValueTaskRepository(private val updateUserValueTaskDao: UpdateUserValueTaskDao) {

    suspend fun list(limit: Int): List<UpdateUserValueTask> {
        return updateUserValueTaskDao.list(limit)
    }

    fun listLive(limit: Int): LiveData<List<UpdateUserValueTask>> {
        return updateUserValueTaskDao.listLive(limit)
    }

    suspend fun find(id: Long): UpdateUserValueTask {
        return updateUserValueTaskDao.find(id)
    }

    /**
     * Creates new task
     *
     * @param updateUserValueTask task
     */
    suspend fun insert(updateUserValueTask: UpdateUserValueTask) {
        updateUserValueTaskDao.insert(updateUserValueTask)
    }

    suspend fun delete(entity: UpdateUserValueTask) {
        updateUserValueTaskDao.delete(entity)
    }

}