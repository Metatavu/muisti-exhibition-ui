package fi.metatavu.muisti.exhibitionui.persistence.dao

import androidx.room.*
import fi.metatavu.muisti.exhibitionui.persistence.model.Layout
import java.util.*

/**
 * DAO class for Layout entity
 */
@Dao
interface LayoutDao {

    /**
     * Inserts new layout into the database
     *
     * @param entity layout entity
     */
    @Insert
    suspend fun insert(entity: Layout)

    /**
     * Finds a layout by id
     *
     * @param layoutId layout id
     * @return found layout or null if not found
     */
    @Query("SELECT * FROM Layout WHERE layoutId = :layoutId")
    suspend fun findByLayoutId(layoutId: UUID): Layout?

    /**
     * Lists all layouts
     *
     * @return list of all layouts from the database
     */
    @Query("SELECT * FROM Layout")
    suspend fun listAll(): List<Layout>?

    /**
     * Updates layout
     *
     * @param entities
     */
    @Update
    suspend fun update(vararg entities: Layout)

    /**
     * Deletes a layout
     *
     * @param entity
     */
    @Delete
    suspend fun delete(entity: Layout)

}