package fi.metatavu.muisti.exhibitionui.visitors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.metatavu.muisti.api.client.models.Visitor

/**
 * Container for currently visible visitors
 */
class VisibleVisitorsContainer {

    companion object {

        private var liveVisibleVisitors = MutableLiveData<List<Visitor>>(listOf())

        /**
         * Returns live data instance for currently visible visitors for the device
         */
        fun getLiveVisibleVisitors(): LiveData<List<Visitor>> {
            return liveVisibleVisitors
        }

        /**
         * Sets currently visible visitors
         *
         * @param visibleVisitors visible visitor list
         */
        fun setVisibleVisitors(visibleVisitors: List<Visitor>) {
            liveVisibleVisitors.postValue(visibleVisitors)
        }

    }

}