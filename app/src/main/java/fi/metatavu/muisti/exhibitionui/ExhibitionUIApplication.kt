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
    private var handler: Handler = Handler()

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

    /**
     * Starts mqtt proximity listening
     */
    private fun startProximityListening() = GlobalScope.launch {
        val antennas =  DeviceSettings.getRfidAntennas()
        Log.d(javaClass.name, "Proximity start")
        if (!antennas.isNullOrEmpty()) {
            for (antenna in antennas) {
                val topic = "${BuildConfig.MQTT_BASE_TOPIC}/${toAntennaPath(antenna)}"
                MqttClientController.addListener(MqttTopicListener(topic, MqttProximityUpdate::class.java) {
                    handleProximityUpdate(it)
                })
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        MuistiMqttService()
        startProximityListening()
    }

    private fun handleProximityUpdate(proximityUpdate: MqttProximityUpdate){
        if (VisitorSessionContainer.getVisitorSessionId() == null && proximityUpdate.strength > 45) {
            // TODO check login threshold from antenna
            if (proximityUpdate.strength > 45) {
                visitorLogin(proximityUpdate.tag)
            }
        } else {
            // TODO check exit threshold from antenna
            if (proximityUpdate.strength > 25) {
                visitorTagDetection(proximityUpdate.tag)
            }
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
            } else {
                val visitorSession = createNewVisitorSession(exhibitionId, tagId)
                if (visitorSession != null) {
                    val visitor = findVisitorWithUserId(exhibitionId, visitorSession.visitorIds[0])
                    if(visitor != null){
                        VisitorSessionContainer.addCurrentVisitor(visitor)
                        restartLogoutTimer()
                    }
                    VisitorSessionContainer.setVisitorSession(visitorSession)
                }
            }
        }
    }

    /**
     * Checks if detected tag belongs to the current session and resets logout timer
     *
     * @param tagId tagId to check for
     */
    private fun visitorTagDetection(tagId: String) = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()

        if (exhibitionId != null) {
            val existingSession = findExistingSession(exhibitionId, tagId)

            if (existingSession != null && existingSession.id == VisitorSessionContainer.getVisitorSession()?.id) {
                restartLogoutTimer()
            }
        }
    }

    /**
     * Resets logout timer
     */
    private fun restartLogoutTimer() {
        handler.removeCallbacksAndMessages("logout")

        // TODO use logout timing from API
        handler.postDelayed({
            logout()
        },"logout", 20000)
        handler.postDelayed({
            logoutWarning()
        },"logout", 10000)
    }

    /**
     * Logs out the current visitor session
     */
    private fun logout() {
        VisitorSessionContainer.setVisitorSession(null)
        val activity = getCurrentActivity()
        if (activity is MuistiActivity) {
            activity.startMainActivity()
        }
    }

    /**
     * Logs out the current visitor session
     */
    private fun logoutWarning() {
        val activity = getCurrentActivity()
        if (activity is MuistiActivity) {
            activity.logoutWarning(20000 - 10000)
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
     * @return Visitor Session or null if creation fails
     */
    private suspend fun createNewVisitorSession(exhibitionId: UUID, tagId: String): VisitorSession? {
        val visitor = findExistingVisitor(exhibitionId = exhibitionId, tagId = tagId) ?: return null
        val visitorSessionApi = MuistiApiFactory.getVisitorSessionsApi()
        val session = VisitorSession(VisitorSessionState.aCTIVE, arrayOf(visitor.id ?: return null))
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