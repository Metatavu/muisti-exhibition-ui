package fi.metatavu.muisti.exhibitionui.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask

/**
 * DAO class for UpdateUserValueTask entity
 */
@Dao
interface UpdateUserValueTaskDao {

    /**
     * Inserts new task into the database
     *
     * @param entity task entity
     */
    @Insert
    suspend fun insert(entity: UpdateUserValueTask)

    @Query("SELECT * FROM UpdateUserValueTask WHERE ID = :id")
    suspend fun find(id: Long): UpdateUserValueTask

    /**
     * Lists all tasks from the database
     *
     * @return all tasks from the database
     */
    @Query ("SELECT * FROM UpdateUserValueTask ORDER BY -priority, time LIMIT :limit")
    suspend fun list(limit: Int): List<UpdateUserValueTask>

    /**
     * Lists all tasks from the database
     *
     * @return all tasks from the database
     */
    @Query ("SELECT * FROM UpdateUserValueTask ORDER BY -priority, time LIMIT :limit")
    fun listLive(limit: Int): LiveData<List<UpdateUserValueTask>>

    @Delete
    suspend fun delete(entity: UpdateUserValueTask)

}