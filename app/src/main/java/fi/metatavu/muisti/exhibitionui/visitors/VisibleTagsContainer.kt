package fi.metatavu.muisti.exhibitionui.visitors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Container for visible tags
 */
class VisibleTagsContainer {

    companion object {

        private var recentlySeenTags = listOf<RecentlySeenTag>()
        private var liveVisibleTags = MutableLiveData<List<String>>(listOf())

        /**
         * Returns live data instance for currently visible tags for the device
         */
        fun getLiveVisibleTags(): LiveData<List<String>> {
            return liveVisibleTags
        }

        /**
         * Updates tag seen status
         *
         * @param tag tag
         * @param visitorSessionEndTimeout timeout when the tag will be determined to be gone
         */
        fun tagSeen(tag: String, visitorSessionEndTimeout: Long) {
            setRecentlySeenTags(recentlySeenTags.filter { it.tag != tag }.plus(RecentlySeenTag(expireTime = System.currentTimeMillis() + visitorSessionEndTimeout, tag = tag)))
        }

        /**
         * Removes recently not seen tags
         */
        fun removeUnseenTags() {
            val now = System.currentTimeMillis()
            setRecentlySeenTags(recentlySeenTags.filter { it.expireTime > now })
        }

        /**
         * Updates list of recently seen tags
         *
         * @param recentlySeenTags list of recently seen tags
         */
        private fun setRecentlySeenTags(recentlySeenTags: List<RecentlySeenTag>) {
            this.recentlySeenTags = recentlySeenTags
            liveVisibleTags.postValue(recentlySeenTags.map { it.tag })
        }

    }

}

/**
 * Private class for storing recently seen tag details
 *
 * @param expireTime time when the tag will expire
 * @param tag tag
 */
private class RecentlySeenTag(val expireTime: Long, val tag: String) { }