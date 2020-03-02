package fi.metatavu.muisti.exhibitionui

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.services.UpdateKeycloakTokenService
import fi.metatavu.muisti.exhibitionui.services.UpdateLayoutsService
import fi.metatavu.muisti.exhibitionui.services.UpdateUserValueService
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors

/**
 * Main application for exhibition UI application
 */
class ExhibitionUIApplication : Application() {

    /**
     * Constructor
     */
    init {
        instance = this
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateKeycloakTokenServiceTask() }, 0, 60, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateUserValueServiceTask() }, 1, 1, TimeUnit.SECONDS)
    }

    /**
     * Enqueues update keycloak token task
     */
    private fun enqueueUpdateKeycloakTokenServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdateKeycloakTokenService::class.java, 1000, serviceIntent)
    }

    /**
     * Enqueues update user value task
     */
    private fun enqueueUpdateUserValueServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdateUserValueService::class.java, 500, serviceIntent)
    }

    /**
     * Enqueues update layouts task
     */
    private fun enqueueUpdateLayoutsServiceTask() {
        val serviceIntent = Intent().apply { }
        JobIntentService.enqueueWork(this, UpdateLayoutsService::class.java, 500, serviceIntent)
    }

    companion object {
        lateinit var instance: ExhibitionUIApplication
    }

}