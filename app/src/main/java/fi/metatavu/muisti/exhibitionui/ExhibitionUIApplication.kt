package fi.metatavu.muisti.exhibitionui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.MqttProximityUpdate
import fi.metatavu.muisti.api.client.models.Visitor
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.services.*
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val antennas = DeviceSettings.getRfidAntennas()
        val device = DeviceSettings.getRfidDevice()

        if (antennas != null && device != null) {
            for (antenna in antennas) {
                val topic = "${BuildConfig.MQTT_BASE_TOPIC}/$device/$antenna/"

                System.out.println("Proximity listen for ${topic}")

                MqttClientController.addListener(MqttTopicListener(topic, MqttProximityUpdate::class.java) {
                    if (it.strength!! > 50) {
                        if (VisitorSessionContainer.getVisitorSessionId() == null) {
                            visitorLogin(it.tag!!)
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

        handler.postDelayed({
            VisitorSessionContainer.setVisitorSessionId(UUID.randomUUID())
        }, 5000)
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
        val visitorSessionsApi = MuistiApiFactory.getVisitorSessionsApi()
        val exhibitionId = DeviceSettings.getExhibitionDeviceId()

        if (exhibitionId != null) {
            val existingSession = findExistingSession(exhibitionId, tagId)

            if (existingSession != null) {
                VisitorSessionContainer.setVisitorSessionId(existingSession.id)
            } else {
                val visitorSession = createNewVisitorSession(exhibitionId, tagId)
                if (visitorSession != null) {
                    VisitorSessionContainer.setVisitorSessionId(visitorSession.id)
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

        val existingSessions = visitorSessionsApi.listVisitorSessions(exhibitionId = exhibitionId, tagId = tagId)
        if (existingSessions.isNotEmpty()) {
            return existingSessions[0]
        }

        return null
    }

    private suspend fun createNewVisitorSession(exhibitionId: UUID, tagId: String): VisitorSession? {
        val visitor = findExistingVisitor(exhibitionId = exhibitionId, tagId = tagId)
    }

    private suspend fun findExistingVisitor(exhibitionId: UUID, tagId: String): Visitor? {
        val visitorsApi = MuistiApiFactory.getVisitorsApi()

        val existingVisitors = visitorsApi.listVisitors(exhibitionId = exhibitionId, tagId = tagId)

        if (existingVisitors.isNotEmpty()) {
            return existingVisitors[0]
        }

        return null
    }


    companion object {
        lateinit var instance: ExhibitionUIApplication
    }

}