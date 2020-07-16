package fi.metatavu.muisti.exhibitionui.persistence.repository

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.models.ExhibitionPage
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.persistence.dao.PageDao
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import java.util.*

/**
 * Repository class for Page
 *
 * @property pageDao PageDao class
 */
class PageRepository(private val pageDao: PageDao) {

    /**
     * Returns a Page
     *
     * @param pageId pageId to match page with
     * @return a page or null if not found
     */
    suspend fun getPage(pageId: UUID): Page? {
        return pageDao.findByPageId(pageId)
    }

    /**
     * Returns all pages from Database
     *
     * @return list of pages
     */
    suspend fun listAll(): List<Page>? {
        return pageDao.listAll()
    }

    /**
     * Sets a Page into the database
     *
     * @param page set Page to database if page with same id exists it will be updated
     */
    suspend fun setPage(page: Page) {
        val entity = pageDao.findByPageId(page.pageId)
        if (entity != null) {
            pageDao.update(page)
        } else {
            pageDao.insert(page)
        }
    }

    /**
     * Deletes a Page from the database
     *
     * @param page Page to be removed from the database
     */
    suspend fun deletePage(page: Page) {
        val entity = pageDao.findByPageId(page.pageId)
        if (entity != null) {
            pageDao.delete(page)
        }
    }

    /**
     * Deletes a Page from the database
     *
     * @param pageId Page to be removed from the database
     */
    suspend fun deletePage(pageId: UUID) {
        val entity = pageDao.findByPageId(pageId)
        if (entity != null) {
            pageDao.delete(entity)
        }
    }

    /**
     * Sets an array of pages into the database
     *
     * @param pages an array of pages to insert into the database if page with same id exists it will be updated
     */
    suspend fun setPages(pages: Array<ExhibitionPage>) {
        pages.forEach {
            val id = it.id
            if (id == null) {
                Log.d(PageRepository::javaClass.name, "id was null")
                return
            }

            val exhibitionId = it.exhibitionId
            if (exhibitionId == null) {
                Log.d(PageRepository::javaClass.name, "exhibitionId was null")
                return
            }

            val existing = pageDao.findByPageId(id)
            val page = Page(
                name = it.name,
                pageId = id,
                exhibitionId = exhibitionId,
                modifiedAt = it.modifiedAt!!,
                resources = it.resources,
                eventTriggers = it.eventTriggers,
                layoutId = it.layoutId,
                enterTransitions = it.enterTransitions,
                exitTransitions = it.exitTransitions
            )
            if (existing == null) {
                pageDao.insert(page)
            } else {
                pageDao.update(page)
            }
        }
    }
}