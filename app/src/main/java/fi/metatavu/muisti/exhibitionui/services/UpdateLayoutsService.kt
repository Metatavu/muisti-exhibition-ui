package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.MqttLayoutCreate
import fi.metatavu.muisti.api.client.models.MqttLayoutDelete
import fi.metatavu.muisti.api.client.models.MqttLayoutUpdate
import fi.metatavu.muisti.api.client.models.PageLayout
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttActionInterface
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.Layout
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.lang.Exception

/**
 * Service for getting layouts from the API
 */
class UpdateLayoutsService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        try {
            UpdateLayouts.updateAllLayouts()
        } catch (e: Exception) {
            Log.d(javaClass.name, "Failed to update layouts", e)
        }
    }
}

object UpdateLayouts : MqttActionInterface {

    override fun getMqttTopicListeners(): List<MqttTopicListener<*>> {
        val layoutUpdateListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/pages/update", MqttLayoutUpdate::class.java) {
            val layoutId = it.id ?: return@MqttTopicListener
            updateLayout(layoutId)
        }
        val layoutCreateListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/pages/create", MqttLayoutCreate::class.java) {
            val layoutId = it.id ?: return@MqttTopicListener
            updateLayout(layoutId)
        }
        val layoutDeleteListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/pages/delete", MqttLayoutDelete::class.java) {
            val layoutId = it.id ?: return@MqttTopicListener
            deleteLayout(layoutId)
        }
        return listOf(layoutCreateListener, layoutUpdateListener, layoutDeleteListener)
    }

    private val layoutRepository: LayoutRepository

    /**
     * Constructor
     */
    init {
        val layoutDao = ExhibitionUIDatabase.getDatabase().layoutDao()
        layoutRepository = LayoutRepository(layoutDao)
    }

    /**
     ' Retrieves all layouts from the API and saves them into the local database
     */
    fun updateAllLayouts(){
        GlobalScope.launch {
            val layouts = MuistiApiFactory.getPageLayoutsApi().listPageLayouts()
            addLayouts(layouts)
            Log.d(javaClass.name, "Updated ${layouts.size} layouts.")
        }
    }


    /**
     * Retrieves a specified layout from the API and saves it into the local database
     *
     * @param id Id of the layout to update from the API
     */
    fun updateLayout(id: UUID){
        GlobalScope.launch {
            val layout = MuistiApiFactory.getPageLayoutsApi().findPageLayout(id)
            addLayouts(arrayOf(layout))
        }
    }

    /**
     * Deletes a specified layout from the local database
     *
     * @param id Id of the layout to delete
     */
    fun deleteLayout(id: UUID){
        GlobalScope.launch {
            layoutRepository.removeLayout(id)
        }
    }

    /**
     * Adds a list of layouts to Database
     *
     * @param layouts an array of layouts to add to the database
     * @return a visitor session for a task
     */
    private suspend fun addLayouts(layouts: Array<PageLayout>) {
        layoutRepository.updateLayouts(layouts)
    }
}