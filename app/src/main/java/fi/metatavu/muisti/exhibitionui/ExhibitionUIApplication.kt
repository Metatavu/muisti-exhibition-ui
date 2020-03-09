package fi.metatavu.muisti.exhibitionui

import android.app.Application
import android.content.Intent
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.services.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateLayoutsServiceTask() }, 1, 15, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdateUserValueServiceTask() }, 1, 1, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueUpdatePagesServiceTask() }, 1, 1, TimeUnit.SECONDS)
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({ enqueueConstructPagesServiceTask() }, 1, 15, TimeUnit.SECONDS)
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

    companion object {
        lateinit var instance: ExhibitionUIApplication
    }

}