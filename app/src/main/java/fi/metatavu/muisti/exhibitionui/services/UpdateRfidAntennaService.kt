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
     * Retrieves all pages from from the currently selected exhibition and saves them to the local database
     */
    fun antennaUpdate(updateExhibitionId: UUID, antennaId: UUID) {
        GlobalScope.launch {
            try {
                val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
                val exhibitionDeviceId = DeviceSettings.getExhibitionDeviceId() ?: return@launch
                if (exhibitionId != updateExhibitionId){
                    return@launch
                }

                val exhibitionDevice = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = exhibitionDeviceId)

                val antenna = MuistiApiFactory.getRfidAntennaApi().findRfidAntenna(exhibitionId = exhibitionId, rfidAntennaId = antennaId)

                if(exhibitionDevice.groupId == antenna.groupId){
                    DeviceSettings.addExhibitionRfidAntenna(antenna)
                } else if (DeviceSettings.hasRfidAntenna(antenna)){
                    DeviceSettings.removeExhibitionRfidAntenna(antenna)
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, "Updating antenna failed", e)
            }
        }
    }

    /**
     * Retrieves all pages from from the currently selected exhibition and saves them to the local database
     */
    fun updateDeviceAntennas(updateExhibitionId: UUID, antennaId: UUID) {
        GlobalScope.launch {
            try {
                val exhibitionId = DeviceSettings.getExhibitionId() ?: return@launch
                val exhibitionDeviceId = DeviceSettings.getExhibitionDeviceId() ?: return@launch
                if (exhibitionId != updateExhibitionId){
                    return@launch
                }

                val exhibitionDevice = MuistiApiFactory.getExhibitionDevicesApi().findExhibitionDevice(exhibitionId = exhibitionId, deviceId = exhibitionDeviceId)

                val antennas = MuistiApiFactory.getRfidAntennaApi().listRfidAntennas(exhibitionId = exhibitionId, deviceGroupId = exhibitionDevice.groupId, roomId = null)


                antennas.forEach {
                    if(exhibitionDevice.groupId == it.groupId){
                        DeviceSettings.addExhibitionRfidAntenna(it)
                    } else if (DeviceSettings.hasRfidAntenna(it)){
                        DeviceSettings.removeExhibitionRfidAntenna(it)
                    }
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, "Updating antenna failed", e)
            }
        }
    }
}