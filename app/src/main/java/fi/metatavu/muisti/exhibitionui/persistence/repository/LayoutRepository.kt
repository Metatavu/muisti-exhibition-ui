package fi.metatavu.muisti.exhibitionui.persistence.repository

import android.content.pm.ActivityInfo
import fi.metatavu.muisti.api.client.infrastructure.Serializer
import fi.metatavu.muisti.api.client.models.DeviceLayout
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.api.client.models.ScreenOrientation
import fi.metatavu.muisti.exhibitionui.persistence.dao.LayoutDao
import fi.metatavu.muisti.exhibitionui.persistence.model.Layout
import java.util.UUID

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
    suspend fun getLayout(layoutId: UUID): Layout? {
        return layoutDao.findByLayoutId(layoutId.toString())
    }

    /**
     * Removes a Layout
     *
     * @param layoutId id of the layout to delete
     */
    suspend fun removeLayout(layoutId: UUID) {
        val entity = getLayout(layoutId)?: return
        layoutDao.delete(entity)
    }

    /**
     * Sets an array of layouts into the database
     *
     * @param layouts an array of layouts to insert into the database if layout with same id exists it will be updated
     */
    suspend fun updateLayouts(layouts: Array<DeviceLayout>) {
        layouts.forEach {
            val id = it.id
            val orientation = getOrientation(it.screenOrientation)

            val existing = layoutDao.findByLayoutId(id.toString())
            val layoutData = getLayoutData(it.data) ?: return@forEach
            if (existing == null) {
                layoutDao.insert(Layout(
                    name = "$id",
                    data = layoutData,
                    layoutId = id,
                    orientation = orientation,
                    modifiedAt = it.modifiedAt
                ))
            } else {
                layoutDao.update(existing.copy(
                    name = "$id",
                    data = layoutData,
                    orientation = orientation,
                    modifiedAt = it.modifiedAt
                ))
            }
        }
    }

    /**
     * Resolves Android screen orientation by API orientation value
     *
     * @param screenOrientation API orientation value
     * @return Android screen orientation
     */
    private fun getOrientation(screenOrientation: ScreenOrientation?): Int {
        if (screenOrientation == ScreenOrientation.landscape) {
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * Converts layout data to a page layout view object
     *
     * @param layoutData layout data
     * @return page layout view object
     */
    private fun getLayoutData(layoutData: Any): PageLayoutView?{
        val pageLayoutViewAdapter = Serializer.moshi.adapter(PageLayoutView::class.java)
        val mapAdapter = Serializer.moshi.adapter(Map::class.java)
        val layoutJson = mapAdapter.toJson(layoutData as Map<*, *>)

        return pageLayoutViewAdapter.fromJson(layoutJson)
    }
}