package fi.metatavu.muisti.exhibitionui.services

import android.util.Log
import fi.metatavu.muisti.api.client.models.MqttLayoutCreate
import fi.metatavu.muisti.api.client.models.MqttLayoutDelete
import fi.metatavu.muisti.api.client.models.MqttLayoutUpdate
import fi.metatavu.muisti.api.client.models.DeviceLayout
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttActionInterface
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import kotlinx.coroutines.*
import java.util.*
import java.lang.Exception

object UpdateLayouts : MqttActionInterface {

    override fun getMqttTopicListeners(): List<MqttTopicListener<*>> {
        val layoutUpdateListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/layouts/update", MqttLayoutUpdate::class.java) {
            val layoutId = it.id ?: return@MqttTopicListener
            Log.d(this.javaClass.name, "Received layout update request for layout $layoutId. Feature is currently not supported")
        }
        val layoutCreateListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/layouts/create", MqttLayoutCreate::class.java) {
            val layoutId = it.id ?: return@MqttTopicListener
            Log.d(this.javaClass.name, "Received layout create request for layout $layoutId. Feature is currently not supported")

        }
        val layoutDeleteListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/layouts/delete", MqttLayoutDelete::class.java) {
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
    fun updateAllLayouts(deviceId: UUID) = GlobalScope.launch {
        try {
            val layouts = MuistiApiFactory.getDeviceDataApi().listDeviceDataLayouts(deviceId = deviceId)
            addLayouts(layouts)
        } catch (e: Exception) {
            Log.e(javaClass.name, "Failed to update all layouts", e)
        }
    }

    /**
     * Deletes a specified layout from the local database
     *
     * @param id Id of the layout to delete
     */
    private fun deleteLayout(id: UUID){
        GlobalScope.launch {
            try {
                layoutRepository.removeLayout(id)
            } catch (e: Exception) {
                Log.e(javaClass.name, "Layout deleting failed", e)
            }
        }
    }

    /**
     * Adds a list of layouts to Database
     *
     * @param layouts an array of layouts to add to the database
     * @return a visitor session for a task
     */
    private suspend fun addLayouts(layouts: Array<DeviceLayout>) {
        layoutRepository.updateLayouts(layouts)
    }
}