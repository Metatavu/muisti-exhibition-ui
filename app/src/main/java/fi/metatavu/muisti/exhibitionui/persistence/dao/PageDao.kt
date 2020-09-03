package fi.metatavu.muisti.exhibitionui.persistence.dao

import androidx.room.*
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import java.util.*

/**
 * DAO class for Page entity
 */
@Dao
interface PageDao {

    /**
     * Inserts new page into the database
     *
     * @param entity page entity
     */
    @Insert
    suspend fun insert(entity: Page)

    /**
     * Finds a page by id
     *
     * @param pageId layout id
     * @return found layout or null if not found
     */
    @Query("SELECT * FROM Page WHERE pageId = :pageId")
    suspend fun findByPageId(pageId: UUID): Page?

    /**
     * Finds a page by order number and language
     *
     * @param language language
     * @param orderNumber order number
     * @return found page or null if not found
     */
    @Query("SELECT * FROM Page WHERE orderNumber = :orderNumber AND language = :language")
    suspend fun findByOrderNumberAndLanguage(orderNumber: Int, language: String): Page?

    /**
     * Lists all pages
     *
     * @return list of all pages from the database
     */
    @Query("SELECT * FROM Page")
    suspend fun listAll(): List<Page>

    /**
     * Lists all page ids
     *
     * @return list of all page ids from the database
     */
    @Query("SELECT pageId FROM Page")
    suspend fun listPageIds(): List<UUID>

    /**
     * Updates page
     *
     * @param entities
     */
    @Update
    suspend fun update(vararg entities: Page)

    /**
     * Deletes a page
     *
     * @param entity
     */
    @Delete
    suspend fun delete(entity: Page)

}