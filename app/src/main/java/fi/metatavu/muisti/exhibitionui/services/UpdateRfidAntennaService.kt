package fi.metatavu.muisti.exhibitionui.services

import android.bluetooth.BluetoothClass
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.mqtt.MqttActionInterface
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

/**
 * Service for getting pages from the API
 */
class UpdateRfidAntennaService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        try {
            UpdateRfidAntenna.updateDeviceAntennas()
        } catch (e: Exception) {
            Log.d(javaClass.name, "Antenna update failed", e)
        }
    }
}

object UpdateRfidAntenna : MqttActionInterface {

    private val pageRepository: PageRepository

    private val antennaUpdateListener = mutableListOf({})

    /**
     * Constructor
     */
    init {
        val pageDao = ExhibitionUIDatabase.getDatabase().pageDao()
        pageRepository = PageRepository(pageDao)
    }

    override fun getMqttTopicListeners(): List<MqttTopicListener<*>> {
        val antennaCreateListener = MqttTopicListener(super.mqttTopic("/rfidantennas/create"), MqttRfidAntennaCreate::class.java) {
            antennaUpdate(it.exhibitionId, it.id)
        }
        val antennaUpdateListener = MqttTopicListener(super.mqttTopic("/rfidantennas/update"), MqttRfidAntennaUpdate::class.java) {
            antennaUpdate(it.exhibitionId, it.id)
        }
        val antennaDeleteListener = MqttTopicListener(super.mqttTopic("/rfidantennas/delete"), MqttRfidAntennaDelete::class.java) {
            antennaUpdate(it.exhibitionId, it.id)
        }

        return listOf(antennaCreateListener, antennaUpdateListener, antennaDeleteListener)
    }

    /**
     * Add Antenna Update Listener
     *
     * Adds a listener that gets triggered when antennas are updated
     */
    fun addAntennaUpdateListener(listener: () -> Unit){
        antennaUpdateListener.add(listener)
    }

    /**
     * Remove Antenna Update Listener
     *
     * Removes listener that gets triggered when antennas are updated
     */
    fun removeAntennaUpdateListener(listener: () -> Unit){
        antennaUpdateListener.remove(listener)
    }

    /**
     * Antenna update
     *
     * Updates device antennas if the incoming update is related to the selected exhibition
     */
    private fun antennaUpdate(updateExhibitionId: UUID, antennaId: UUID) {
        GlobalScope.launch {
            try {
                val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
                val exhibitionDeviceId = DeviceSettings.getExhibitionDeviceId() ?: return@launch
                if (exhibitionId != updateExhibitionId) {
                    return@launch
                }

                val exhibitionDevice = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = exhibitionDeviceId)

                val antenna = MuistiApiFactory.getRfidAntennaApi().findRfidAntenna(exhibitionId = exhibitionId, rfidAntennaId = antennaId)

                if (exhibitionDevice.groupId == antenna.groupId || DeviceSettings.hasRfidAntenna(antenna)) {
                    updateDeviceAntennas()
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, "Updating antenna failed", e)
            }
        }
    }

    /**
     * Updates device antennas for the currently selected device
     */
    fun updateDeviceAntennas() {
        GlobalScope.launch {
            try {
                val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
                val exhibitionDeviceId = DeviceSettings.getExhibitionDeviceId() ?: return@launch
                val exhibitionDevice = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = exhibitionDeviceId)
                val antennas = MuistiApiFactory.getRfidAntennaApi().listRfidAntennas(exhibitionId = exhibitionId, deviceGroupId = exhibitionDevice.groupId, roomId = null)
                DeviceSettings.removeAllExhibitionRfidAntennas()
                val list: List<RfidAntenna> = antennas.toList()
                DeviceSettings.setExhibitionAntennaList(list)
                antennaUpdateListener.forEach { it.invoke() }
            } catch (e: Exception) {
                Log.e(javaClass.name, "Updating antenna failed", e)
            }
        }
    }
}