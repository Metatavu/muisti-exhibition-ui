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


    private fun startProximityListening() = GlobalScope.launch {
        val antennas =  listOf("88120480/1/","88120480/2/","16250009/1/","16250009/2/")//DeviceSettings.getRfidAntennas()
        Log.d(javaClass.name, "Proximity start")
        if (!antennas.isNullOrEmpty()) {
            for (antenna in antennas) {
                val topic = "${BuildConfig.MQTT_BASE_TOPIC}/$antenna"
                MqttClientController.addListener(MqttTopicListener(topic, MqttProximityUpdate::class.java) {
                    Log.d(javaClass.name, "Proximity recieved message ${it}")
                    if (it.strength > 35) {
                        if (VisitorSessionContainer.getVisitorSessionId() == null) {
                            visitorLogin(it.tag)
                        }
                    }
                })
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        MuistiMqttService()
        startProximityListening()
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

    private fun visitorLogin(tagId: String) = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()

        if (exhibitionId != null) {
            val existingSession = findExistingSession(exhibitionId, tagId)

            if (existingSession != null) {
                VisitorSessionContainer.setVisitorSession(existingSession)
            } else {
                val visitorSession = createNewVisitorSession(exhibitionId, tagId)
                if (visitorSession != null) {
                    val visitor = findVisitor(exhibitionId, visitorSession.visitorIds[0])
                    if(visitor != null){
                        VisitorSessionContainer.addCurrentVisitor(visitor)
                    }
                    VisitorSessionContainer.setVisitorSession(visitorSession)
                }
            }
            /**
            val existingSessions = visitorSessionsApi.listVisitorSessions(exhibitionId = exhibitionId, tagId = tagId)
            if (existingSessions.isNotEmpty()) {
                VisitorSessionContainer.setVisitorSessionId(existingSessions[0].id)
            } else {

            }
            **/
        }

        // Attempt to find existing visitor session
        /// If available login
        // If not create new (if permitted)
    }

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

    private suspend fun createNewVisitorSession(exhibitionId: UUID, tagId: String): VisitorSession? {
        val visitor = findExistingVisitor(exhibitionId = exhibitionId, tagId = tagId) ?: return null
        val visitorSessionApi = MuistiApiFactory.getVisitorSessionsApi()
        val session = VisitorSession(VisitorSessionState.aCTIVE, arrayOf(visitor.id ?: return null))
        return visitorSessionApi.createVisitorSession(exhibitionId, session)
    }

    private suspend fun findExistingVisitor(exhibitionId: UUID, tagId: String): Visitor? {
        val visitorsApi = MuistiApiFactory.getVisitorsApi()

        val existingVisitors = visitorsApi.listVisitors(exhibitionId = exhibitionId, tagId = tagId)

        if (existingVisitors.isNotEmpty()) {
            return existingVisitors[0]
        }

        return null
    }

    private suspend fun findVisitor(exhibitionId: UUID, userId: UUID): Visitor? {
        val visitorsApi = MuistiApiFactory.getVisitorsApi()

        return visitorsApi.findVisitor(exhibitionId = exhibitionId, visitorId = userId)
    }


    companion object {
        lateinit var instance: ExhibitionUIApplication
    }

}