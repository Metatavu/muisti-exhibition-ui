package fi.metatavu.muisti.exhibitionui.persistence.repository

import fi.metatavu.muisti.api.client.models.ExhibitionPageLayout
import fi.metatavu.muisti.exhibitionui.persistence.dao.LayoutDao
import fi.metatavu.muisti.exhibitionui.persistence.model.Layout

/**
 * Repository class for Layout
 *
 * @property layoutDao LayoutDao class
 */
class LayoutRepository(private val layoutDao: LayoutDao) {

    /**
     * Returns a Layout
     *
     * @param layoutId layoutId to match layout with
     * @return a layout or null if not found
     */
    suspend fun getLayout(layoutId: String): Layout? {
        return layoutDao.findByLayoutId(layoutId)
    }

    /**
     * Returns all layouts from Database
     *
     * @return a device setting value or null if not found
     */
    suspend fun listAll(): List<Layout>? {
        return layoutDao.listAll()
    }

    /**
     * Sets a Layout into the database
     *
     * @param layout set Layout to database if layout with same id exists it will be updated
     */
    suspend fun setLayout(layout: Layout) {
        val entity = layoutDao.findByLayoutId(layout.layoutId)
        if (entity != null) {
            layoutDao.update(layout)
        } else {
            layoutDao.insert(layout)
        }
    }

    /**
     * Sets an array of layouts into the database
     *
     * @param layouts an array of layouts to insert into the database if layout with same id exists it will be updated
     */
    suspend fun setLayouts(layouts: Array<ExhibitionPageLayout>) {
        layouts.forEach {
            val existing = layoutDao.findByLayoutId(it.id.toString())
            val layout = Layout(
                it.name,
                it.data,
                it.id.toString(),
                it.exhibitionId.toString(),
                it.modifiedAt!!
            )
            if (existing == null) {
                layoutDao.insert(layout)
            } else {
                layoutDao.update(layout)
            }
        }
    }
}