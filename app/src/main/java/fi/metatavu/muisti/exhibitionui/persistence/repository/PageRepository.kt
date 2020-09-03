package fi.metatavu.muisti.exhibitionui.persistence.repository

import android.util.Log
import fi.metatavu.muisti.api.client.models.ContentVersion
import fi.metatavu.muisti.api.client.models.ExhibitionPage
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
     * Finds an index page for given language
     *
     * @param language language
     * @return index page for given language or null if not found
     */
    suspend fun findIndexPage(language: String): Page? {
        return pageDao.findByOrderNumberAndLanguage(orderNumber = 0, language = language)
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
     * Sets an array of pages into the database and removes all other pages
     *
     * @param pages an array of pages to insert into the database if page with same id exists it will be updated
     * @param contentVersions an array of content versions related to pages
     */
    suspend fun setPages(pages: Array<ExhibitionPage>, contentVersions: Array<ContentVersion>) {
        val existingPageIds = pageDao.listPageIds()
        val deleteIds = existingPageIds.minus(pages.map { it.id!! })

        deleteIds.forEach { pageId -> deletePage(pageId = pageId) }
        pages.forEach { page -> updatePage(page = page, contentVersion = contentVersions.first { contentVersion -> contentVersion.id == page.contentVersionId }) }
    }

    /**
     * Updates single page into the database
     *
     * @param page page
     * @param contentVersion content version of the page
     */
    suspend fun updatePage(page: ExhibitionPage, contentVersion: ContentVersion) {
        val id = page.id
        if (id == null) {
            Log.d(PageRepository::javaClass.name, "id was null")
            return
        }

        val exhibitionId = page.exhibitionId
        if (exhibitionId == null) {
            Log.d(PageRepository::javaClass.name, "exhibitionId was null")
            return
        }

        val updatePage = Page(
            name = page.name,
            pageId = id,
            language = contentVersion.language,
            orderNumber = page.orderNumber,
            exhibitionId = exhibitionId,
            modifiedAt = page.modifiedAt!!,
            resources = page.resources,
            eventTriggers = page.eventTriggers,
            layoutId = page.layoutId,
            enterTransitions = page.enterTransitions,
            exitTransitions = page.exitTransitions
        )

        if (pageDao.findByPageId(id) == null) {
            pageDao.insert(updatePage)
        } else {
            pageDao.update(updatePage)
        }

    }
}