package fi.metatavu.muisti.exhibitionui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.services.*
import fi.metatavu.muisti.exhibitionui.visitors.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.visitors.VisibleVisitorsContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import fi.metatavu.muisti.exhibitionui.visitors.ExhibitionVisitorsContainer
import fi.metatavu.muisti.exhibitionui.visitors.VisibleTagsContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Main application for exhibition UI application
 */
class ExhibitionUIApplication : Application() {

    private var currentActivity: Activity? = null
    private val visitorSessionHandler = Handler()
    private val unseenTagsHandler = Handler()
    private var visitorSessionEndTimeout: Long = 5000
    private var allowVisitorSessionCreation = false
    private var antennaListeners = emptyList<MqttTopicListener<*>>()
    var forcedPortraitMode: Boolean? = null
        private set

    /**
     * Constructor
     */
    init {
        instance = this
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateKeycloakTokenServiceTask() }, 1, 5, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateLayoutsServiceTask() }, 5, 15, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateUserValueServiceTask() }, 5, 1, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdatePagesServiceTask() }, 5, 15, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueConstructPagesServiceTask() }, 5, 15, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateVisitorsServiceTask() }, 5, 5, TimeUnit.SECONDS)

        VisibleTagsContainer.getLiveVisibleTags().observeForever {
            onVisibleTagsChange(it)
        }

        VisitorSessionContainer.getLiveVisitorSession().observeForever {
            onVisitorSessionChange(it)
        }
    }

    override fun onCreate() {
        super.onCreate()
        readApiValues()
        MuistiMqttService()
        startProximityListening()
        pollUnseenTags()

        UpdateRfidAntenna.addAntennaUpdateListener {
            restartProximityListening()
        }
    }

    /**
     * Returns currently active activity
     *
     * @return currently active activity
     */
    fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    /**
     * Sets currently active activity
     *
     * @param activity activity
     */
    fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    /**
     * Logs out the current visitor session
     */
    private fun endVisitorSession() {
        visitorSessionHandler.removeCallbacksAndMessages(null)
        VisitorSessionContainer.endVisitorSession()
        readApiValues()

        val activity = getCurrentActivity()
        if (activity is PageActivity) {
            activity.startMainActivity()
            currentActivity = null
        }
    }

    /**
     * Starts mqtt proximity listening
     */
    private fun startProximityListening() = GlobalScope.launch {
        antennaListeners = DeviceSettings.getRfidAntennas().flatMap { antenna ->
            getAntennaTopics(antenna).map { topic ->
                Log.d(javaClass.name, "Proximity start listener started for topic $topic with start threshold ${antenna.visitorSessionStartThreshold}")

                MqttClientController.addListener(MqttTopicListener(topic, MqttProximityUpdate::class.java) { proximityUpdate ->
                    handleProximityUpdate(antenna = antenna, proximityUpdate = proximityUpdate)
                })
            }
        }
    }

    /**
     * Restarts mqtt proximity listening
    */
    private fun restartProximityListening() = GlobalScope.launch {
        MqttClientController.removeListeners(antennaListeners)
        startProximityListening()
    }

    /**
     * Polls recently seen tags list and removes tags that have not been seen recently
     */
    private fun pollUnseenTags() {
        unseenTagsHandler.postDelayed({
            VisibleTagsContainer.removeUnseenTags()
            pollUnseenTags()
        }, visitorSessionEndTimeout / 2)
    }

    /**
     *  Reads visitor session end time out and forced portrait mode values from API
     */
    fun readApiValues() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()
        val deviceId = DeviceSettings.getExhibitionDeviceId()

        if (exhibitionId == null) {
            Log.e(javaClass.name, "Exhibition not configured. Using default visitor session end timeout")
        } else if (deviceId == null) {
            Log.e(javaClass.name, "Device not configured. Using default visitor session end timeout")
        } else {
            try {
                val device = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = deviceId)
                val group = MuistiApiFactory.getExhibitionDeviceGroupsApi().findExhibitionDeviceGroup(exhibitionId = exhibitionId, deviceGroupId = device.groupId)

                forcedPortraitMode = device.screenOrientation == ScreenOrientation.forcedPortrait
                visitorSessionEndTimeout = group.visitorSessionEndTimeout
                allowVisitorSessionCreation = group.allowVisitorSessionCreation

                Log.d(javaClass.name, "Device orientation is set to: ${device.screenOrientation}")
                Log.d(javaClass.name, "Visitor session end timeout set to: $visitorSessionEndTimeout")
                Log.d(javaClass.name, "Allow visitor session creation is set to: $allowVisitorSessionCreation")
            } catch (e: Exception) {
                Log.e(javaClass.name, "Could not read device settings from API", e)
            }
        }
    }

    /**
     * Handles a proximity update message
     *
     * @param antenna antenna that reported the proximity update
     * @param proximityUpdate proximity update message
     */
    private fun handleProximityUpdate(antenna: RfidAntenna, proximityUpdate: MqttProximityUpdate) {
        if (proximityUpdate.strength > antenna.visitorSessionStartThreshold || VisitorSessionContainer.getVisitorSession() != null &&
                proximityUpdate.strength > antenna.visitorSessionEndThreshold) {
            VisibleTagsContainer.tagSeen(tag = proximityUpdate.tag, visitorSessionEndTimeout = visitorSessionEndTimeout)
        }
    }

    /**
     * Enqueues update keycloak token task
     */
    private fun enqueueUpdateKeycloakTokenServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdateKeycloakTokenService::class.java, 1, serviceIntent)
    }

    /**
     * Enqueues update user value task
     */
    private fun enqueueUpdateUserValueServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdateUserValueService::class.java, 2, serviceIntent)
    }

    /**
     * Enqueues update layouts task
     */
    private fun enqueueUpdateLayoutsServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdateLayoutsService::class.java, 3, serviceIntent)
    }

    /**
     * Enqueues update pages task
     */
    private fun enqueueUpdatePagesServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdatePagesService::class.java, 4, serviceIntent)
    }

    /**
     * Enqueues construct pages task
     */
    private fun enqueueConstructPagesServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, ConstructPagesService::class.java, 5, serviceIntent)
    }

    /**
     * Enqueues update visitors service task
     */
    private fun enqueueUpdateVisitorsServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, VisitorsService::class.java, 6, serviceIntent)
    }

    /**
     * Resets visitor session end timer
     */
    private fun resetVisitorSessionEndTimer() {
        visitorSessionHandler.removeCallbacksAndMessages(null)

        visitorSessionHandler.postDelayed({
            endVisitorSession()
        }, visitorSessionEndTimeout)

        visitorSessionHandler.postDelayed({
            logoutWarning()
        }, visitorSessionEndTimeout / 2)
    }

    /**
     * Resets the Logout timers and hides the Logout warning toast.
     */
    fun onInteraction() {
        val activity = getCurrentActivity()
        if (activity is PageActivity) {
            activity.cancelLogoutWarning()
        }

        resetVisitorSessionEndTimer()
    }

    /**
     * Sends a logout warning to the current activity.
     */
    private fun logoutWarning() {
        val activity = getCurrentActivity()
        if (activity is PageActivity) {
            activity.logoutWarning(visitorSessionEndTimeout / 2)
        }
    }

    /**
     * Returns topics for given antenna
     *
     * @param rfidAntenna rfidAntenna
     * @return topics
     */
    private fun getAntennaTopics(rfidAntenna: RfidAntenna): Array<String> {
        val baseTopic = "${BuildConfig.MQTT_BASE_TOPIC}/${rfidAntenna.readerId}/${rfidAntenna.antennaNumber}"
        return arrayOf(baseTopic, "$baseTopic/")
    }

    /**
     * Returns all tags associated with given visitor session
     *
     * @param visitorSession visitor session
     * @return tags associated with given visitor session
     */
    private fun getVisitorSessionTags(visitorSession: VisitorSession): List<String> {
        return visitorSession.visitorIds
            .mapNotNull { ExhibitionVisitorsContainer.findVisitorById(it) }
            .map(Visitor::tagId)
    }

    /**
     * Refresh visitor session state with given tags
     *
     * @param tags tags
     */
    private fun refreshVisitorSessionState(tags: List<String>) {
        val currentVisitorSession = VisitorSessionContainer.getVisitorSession()
        if (currentVisitorSession == null) {
            if (tags.isNotEmpty()) {
                val visitorSession = ExhibitionVisitorsContainer.findVisitorSessionByTags(tags = tags)
                if (visitorSession != null) {
                    Log.d(javaClass.name, "Visitor session ${visitorSession.id} found for tags ${tags.joinToString(",")}")
                    val visitorSessionTags = getVisitorSessionTags(visitorSession = visitorSession)
                    VisitorSessionContainer.startVisitorSession(visitorSession, visitorSessionTags)
                } else {
                    Log.d(javaClass.name, "Visitor session not active for any of the tags ${tags.joinToString(",")}")
                }
            }
        } else {
            val visitorSessionTags = VisitorSessionContainer.getVisitorSessionTags()
            if (visitorSessionTags.any { it in tags }) {
                resetVisitorSessionEndTimer()
            }
        }
    }

    /**
     * Handler for changes in tags visible to the device
     *
     * @param tags tags currently visible to the device
     */
    private fun onVisibleTagsChange(tags: List<String>) {
        GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId()
            if (exhibitionId != null) {
                VisibleVisitorsContainer.setVisibleVisitors(tags.mapNotNull { ExhibitionVisitorsContainer.findVisitorByTag(tag = it) })

                if (!allowVisitorSessionCreation) {
                    refreshVisitorSessionState(tags = tags)
                }
            }
        }
    }

    /**
     * Handler for visitor session changes
     *
     * @param visitorSession visitor session
     */
    private fun onVisitorSessionChange(visitorSession: VisitorSession?) {
        if (visitorSession == null) {
            endVisitorSession()
            Log.d(javaClass.name, "Visitor session has ended")
        } else {
            resetVisitorSessionEndTimer()
            Log.d(javaClass.name, "Visitor session ${visitorSession.id} still active")
        }
    }

    companion object {

        lateinit var instance: ExhibitionUIApplication

    }
}