package fi.metatavu.muisti.exhibitionui.visitors

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.metatavu.muisti.api.client.models.VisitorSessionV2
import fi.metatavu.muisti.api.client.models.VisitorSessionVariable

/**
 * Container for visitor session
 */
class VisitorSessionContainer {

    companion object {

        private var currentVisitorSessionTags: List<String> = listOf()
        private var liveVisitorSession = MutableLiveData<VisitorSessionV2?>(null)

        /**
         * Returns live data instance for current visitor session
         *
         * @return live data instance for current visitor session
         */
        fun getLiveVisitorSession(): LiveData<VisitorSessionV2?> {
            return liveVisitorSession
        }

        /**
         * Starts new visitor session
         *
         * @param visitorSession visitor session
         * @param visitorSessionTags visitor session's tags
         */
        fun startVisitorSession(visitorSession: VisitorSessionV2, visitorSessionTags: List<String> ) {
            Log.d(VisitorSessionContainer::class.java.name, "Visitor session ${visitorSession.id} started")
            this.currentVisitorSessionTags = visitorSessionTags
            this.setVisitorSession(visitorSession)
        }

        /**
         * Ends visitor session
         */
        fun endVisitorSession() {
            this.currentVisitorSessionTags = listOf()
            this.setVisitorSession(null)
        }

        /**
         * Returns current visitor session
         *
         * @return current visitor session
         */
        fun getVisitorSession(): VisitorSessionV2? {
            return liveVisitorSession.value
        }

        /**
         * Returns tags for current visitor session
         *
         * @return tags for current visitor session
         */
        fun getVisitorSessionTags(): List<String> {
            return this.currentVisitorSessionTags
        }

        /**
         * Updates visitor session variable into existing session.
         *
         * This method updates only local visitor session and DOES NOT save variable into the server
         *
         * @param name name of the variable
         * @param value new value
         */
        fun setVisitorSessionUserVariable(name: String, value: String?) {
            val visitorSession = this.liveVisitorSession.value
            if (visitorSession != null) {
                var variables = (visitorSession.variables ?: emptyArray()).filter { it.name != name }.toTypedArray()

                if (value != null) {
                    variables = variables.plus(VisitorSessionVariable(name = name, value = value))
                }

                liveVisitorSession.postValue(visitorSession.copy(variables = variables))
            }
        }

        /**
         * Updates current visitor session if needed
         *
         * @param visitorSession visitor session
         */
        private fun setVisitorSession(visitorSession: VisitorSessionV2?) {
            if (getVisitorSession()?.id != visitorSession?.id) {
                liveVisitorSession.postValue(visitorSession)
            }
        }
    }
}
