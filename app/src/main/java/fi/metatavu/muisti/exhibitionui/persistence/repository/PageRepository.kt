package fi.metatavu.muisti.exhibitionui.persistence.repository

import fi.metatavu.muisti.api.client.models.DevicePage
import fi.metatavu.muisti.exhibitionui.persistence.dao.PageDao
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import java.util.UUID

/**
 * Repository class for Page
 *
 * @property pageDao PageDao class
 */
class PageRepository(private val pageDao: PageDao) {

    /**
     * Lists index pages for given language
     *
     * @param language language
     * @return index pages for given language
     */
    suspend fun listIndexPages(language: String): List<Page> {
        return pageDao.listByOrderNumberAndLanguage(orderNumber = 0, language = language)
    }

    /**
     * Returns all pages from Database
     *
     * @return list of pages
     */
    suspend fun listAll(): List<Page> {
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
     */
    suspend fun setPages(pages: Array<DevicePage>) {
        val existingPageIds = pageDao.listPageIds()
        val deleteIds = existingPageIds.minus(pages.map { it.id }.toSet())

        deleteIds.forEach { pageId -> deletePage(pageId = pageId) }
        pages.forEach { page -> updatePage(page = page) }
    }

    /**
     * Updates single page into the database
     *
     * @param page page
     * @return updated pages
     */
    private suspend fun updatePage(page: DevicePage): Page {
        val id = page.id
        val exhibitionId = page.exhibitionId

        val updatePage = Page(
            name = page.name ?: "$id",
            pageId = id,
            language = page.language,
            orderNumber = page.orderNumber,
            exhibitionId = exhibitionId,
            modifiedAt = page.modifiedAt,
            resources = page.resources,
            activeConditionUserVariable = page.activeConditionUserVariable,
            activeConditionEquals = page.activeConditionEquals,
            eventTriggers = page.eventTriggers ?: emptyArray(),
            layoutId = page.layoutId,
            enterTransitions = page.enterTransitions ?: emptyArray(),
            exitTransitions = page.exitTransitions ?: emptyArray()
        )

        if (pageDao.findByPageId(id) == null) {
            pageDao.insert(updatePage)
        } else {
            pageDao.update(updatePage)
        }

        return updatePage
    }
}