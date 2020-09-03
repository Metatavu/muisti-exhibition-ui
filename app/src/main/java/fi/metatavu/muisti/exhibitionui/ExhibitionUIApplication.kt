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
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Main application for exhibition UI application
 */
class ExhibitionUIApplication : Application() {

    private var currentActivity: Activity? = null
    private val handler = Handler()
    private var visitorSessionEndTimeout: Long = 5000

    /**
     * Constructor
     */
    init {
        instance = this
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateKeycloakTokenServiceTask() }, 1, 5, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateLayoutsServiceTask() }, 1, 15, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateUserValueServiceTask() }, 1, 1, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdatePagesServiceTask() }, 1, 4, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueConstructPagesServiceTask() }, 1, 15, TimeUnit.SECONDS)
    }

    override fun onCreate() {
        super.onCreate()
        MuistiMqttService()

        startProximityListening()
        readVisitorSessionEndTimeout()
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
     * Starts mqtt proximity listening
     */
    private fun startProximityListening() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()
        val deviceId = DeviceSettings.getExhibitionDeviceId()

        if (exhibitionId == null) {
            Log.e(javaClass.name, "Exhibition not configured. Cannot start proximity updates")
        } else if (deviceId == null) {
            Log.e(javaClass.name, "Device not configured. Cannot start proximity updates")
        } else {
            val device = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = deviceId)
            val antennas = MuistiApiFactory.getRfidAntennaApi().listRfidAntennas(exhibitionId = exhibitionId, deviceGroupId = device.groupId, roomId = null)

            Log.d(javaClass.name, "Proximity listeners starting...")

            antennas.forEach {
                    antenna -> run {
                val topic = "${BuildConfig.MQTT_BASE_TOPIC}/${toAntennaPath(antenna)}"
                Log.d(javaClass.name, "Proximity start listener started for topic $topic")

                MqttClientController.addListener(MqttTopicListener(topic, MqttProximityUpdate::class.java) {
                        proximityUpdate -> handleProximityUpdate(antenna = antenna, proximityUpdate = proximityUpdate)
                })
            }
            }
        }
    }

    /**
     * Reads visitors session end timeout from API
     */
    private fun readVisitorSessionEndTimeout() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()
        val deviceId = DeviceSettings.getExhibitionDeviceId()

        if (exhibitionId == null) {
            Log.e(javaClass.name, "Exhibition not configured. Using default visitor session end timeout")
        } else if (deviceId == null) {
            Log.e(javaClass.name, "Device not configured. Using default visitor session end timeout")
        } else {
            val device = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = deviceId)
            val group = MuistiApiFactory.getExhibitionDeviceGroupsApi().findExhibitionDeviceGroup(exhibitionId = exhibitionId, deviceGroupId = device.groupId)
            visitorSessionEndTimeout = group.visitorSessionEndTimeout
            Log.d(javaClass.name, "Visitor session end timeout set to $visitorSessionEndTimeout")
        }
    }

    /**
     * Handles a proximity update message
     *
     * @param antenna antenna that reported the proximity update
     * @param proximityUpdate proximity update message
     */
    private fun handleProximityUpdate(antenna: RfidAntenna, proximityUpdate: MqttProximityUpdate) {
        if (VisitorSessionContainer.getVisitorSessionId() == null) {
            if (proximityUpdate.strength > antenna.visitorSessionStartThreshold) {
                visitorLogin(proximityUpdate.tag)
            }
        } else {
            if (proximityUpdate.strength > antenna.visitorSessionEndThreshold) {
                visitorTagDetection(proximityUpdate.tag)
            }
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
     * Attempts to log the visitor in
     *
     * @param tagId tagId to attempt to log in with
     */
    fun visitorLogin(tagId: String) = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()

        if (exhibitionId != null) {
            val existingSession = findExistingSession(exhibitionId, tagId)

            if (existingSession != null) {
                VisitorSessionContainer.setVisitorSession(existingSession)
                restartLogoutTimer()
            } else {
                // TODO: Resolve language
                val visitorSession = createNewVisitorSession(exhibitionId = exhibitionId, tagId = tagId, language = "FI")
                if (visitorSession != null) {
                    val visitor = findVisitorWithUserId(exhibitionId, visitorSession.visitorIds[0])
                    if(visitor != null){
                        VisitorSessionContainer.addCurrentVisitor(visitor)
                    }
                    VisitorSessionContainer.setVisitorSession(visitorSession)
                    restartLogoutTimer()
                }
            }
        }
    }

    /**
     * Checks if detected tag belongs to the current session and triggers interaction.
     *
     * @param tagId tagId to check for
     */
    private fun visitorTagDetection(tagId: String) = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()

        if (exhibitionId != null) {
            val existingSession = findExistingSession(exhibitionId, tagId)

            if (existingSession != null && existingSession.id == VisitorSessionContainer.getVisitorSession()?.id) {
                onInteraction()
            }
        }
    }

    /**
     * Resets logout timer
     */
    private fun restartLogoutTimer() {
        handler.removeCallbacksAndMessages(null)

        handler.postDelayed({
            logout()
        },null, visitorSessionEndTimeout)

        handler.postDelayed({
            logoutWarning()
        },null, visitorSessionEndTimeout / 2)
    }

    /**
     * Resets the Logout timers and hides the Logout warning toast.
     */
    fun onInteraction() {
        val activity = getCurrentActivity()
        if (activity is MuistiActivity) {
            activity.cancelLogoutWarning()
        }

        restartLogoutTimer()
    }

    /**
     * Logs out the current visitor session
     */
    private fun logout() {
        handler.removeCallbacksAndMessages(null)
        VisitorSessionContainer.setVisitorSession(null)
        VisitorSessionContainer.clearCurrentVisitors()
        val activity = getCurrentActivity()
        if (activity is MuistiActivity) {
            activity.startMainActivity()
        }
        currentActivity = null
    }

    /**
     * Logs out the current visitor session
     */
    private fun logoutWarning() {
        val activity = getCurrentActivity()
        if (activity is MuistiActivity) {
            activity.logoutWarning(visitorSessionEndTimeout / 2)
        }
    }

    /**
     * Attempts to find an existing visitor session in exhibition with users tag
     *
     * @param exhibitionId ExhibitionId to find the session with
     * @param tagId Tag to find the session with
     * @return Visitor session or null if not found
     */
    private suspend fun findExistingSession(exhibitionId: UUID, tagId: String): VisitorSession? {
        val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()
        try {
            val existingSessions = visitorSessionsApi.listVisitorSessions(exhibitionId = exhibitionId, tagId = tagId)
            if (existingSessions.isNotEmpty()) {
                return existingSessions[0]
            }
        } catch (e: Exception){
            Log.e(javaClass.name, "Cannot get existing session response: $e")
        }
        return null
    }

    /**
     * Creates a new visitor session
     *
     * @param exhibitionId exhibition to create the session in
     * @param tagId tag to add into the session
     * @param language language
     * @return Visitor Session or null if creation fails
     */
    private suspend fun createNewVisitorSession(exhibitionId: UUID, tagId: String, language: String): VisitorSession? {
        val visitor = findExistingVisitor(exhibitionId = exhibitionId, tagId = tagId) ?: return null
        val visitorSessionApi = MuistiApiFactory.getVisitorSessionsApi()

        val session = VisitorSession(
            state = VisitorSessionState.aCTIVE,
            language = language,
            visitorIds = arrayOf(visitor.id ?: return null)
        )

        return visitorSessionApi.createVisitorSession(exhibitionId, session)
    }

    /**
     * Finds existing visitor with tag
     *
     * @param exhibitionId exhibition to find the visitor in
     * @param tagId tagId to find the visitor with
     * @return Visitor or null if not found
     */
    private suspend fun findExistingVisitor(exhibitionId: UUID, tagId: String): Visitor? {
        val visitorsApi = MuistiApiFactory.getVisitorsApi()

        val existingVisitors = visitorsApi.listVisitors(exhibitionId = exhibitionId, tagId = tagId)

        if (existingVisitors.isNotEmpty()) {
            return existingVisitors[0]
        }

        return null
    }

    /**
     * Finds existing visitor with userId
     *
     * @param exhibitionId exhibition to find the visitor in
     * @param userId userId to find the visitor with
     * @return Visitor or null if not found
     */
    private suspend fun findVisitorWithUserId(exhibitionId: UUID, userId: UUID): Visitor? {
        val visitorsApi = MuistiApiFactory.getVisitorsApi()
        return visitorsApi.findVisitor(exhibitionId = exhibitionId, visitorId = userId)
    }

    /**
     * Returns rfidAntenna as an mqtt path.
     *
     * @param rfidAntenna rfidAntenna
     * @return UUID or null if string is null
     */
    private fun toAntennaPath(rfidAntenna: RfidAntenna): String {
        return "${rfidAntenna.readerId}/${rfidAntenna.antennaNumber}/"
    }


    companion object {
        lateinit var instance: ExhibitionUIApplication
    }
}