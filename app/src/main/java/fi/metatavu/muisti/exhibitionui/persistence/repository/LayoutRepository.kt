package fi.metatavu.muisti.exhibitionui.persistence.repository

import android.util.Log
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
     * Sets a Layout
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

    suspend fun setLayouts(layouts: Array<ExhibitionPageLayout>) {
        layouts.forEach {
            val existing = layoutDao.findByLayoutId(it.id.toString())
            if(existing == null){
                val layout = Layout(
                    it.name,
                    it.data,
                    it.id.toString(),
                    it.exhibitionId.toString(),
                    it.creatorId.toString(),
                    it.lastModifierId.toString(),
                    it.createdAt,
                    it.modifiedAt
                )
                Log.d(javaClass.name, layout.name)
                layoutDao.insert(layout)
            }
        }
    }
}