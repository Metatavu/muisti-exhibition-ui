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
     * Lists update user value tasks from database
     *
     * @param limit limit result count
     * @return update user value tasks
     */
    suspend fun list(limit: Int): List<UpdateUserValueTask> {
        return updateUserValueTaskDao.list(limit)
    }

    /**
     * Lists update user value tasks from database returns live data instance
     *
     * @param limit limit result count
     * @return live data for update user value tasks
     */
    fun listLive(limit: Int): LiveData<List<UpdateUserValueTask>> {
        return updateUserValueTaskDao.listLive(limit)
    }

    /**
     * Finds a update user value task by id
     *
     * @param id id
     * @return update user value task
     */
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

    /**
     * Deletes an update user value task
     *
     * @param entity update user value task
     */
    suspend fun delete(entity: UpdateUserValueTask) {
        updateUserValueTaskDao.delete(entity)
    }

}