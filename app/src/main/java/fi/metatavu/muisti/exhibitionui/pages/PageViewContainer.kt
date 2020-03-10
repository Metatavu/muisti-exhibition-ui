package fi.metatavu.muisti.exhibitionui.pages

import android.view.View
import java.util.*

/**
 * Page view container
 */
class PageViewContainer {

    companion object {

        private val layoutMap= mutableMapOf<UUID, View>()

        /**
         * Returns constructed view by view id
         *
         * @param id view id
         * @return constructed view or null if not found
         */
        fun get(id: UUID): View? {
            return layoutMap[id]
        }

        /**
         * Stored constructed view container
         *
         * @param id view id
         * @param view constructed view
         */
        fun set(id: UUID, view: View){
            layoutMap[id] = view
        }
    }

}