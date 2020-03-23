package fi.metatavu.muisti.exhibitionui.services

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.infrastructure.UUIDAdapter
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.mqtt.MuistiMqttCallBack
import fi.metatavu.muisti.exhibitionui.mqtt.MuistiMqttClient
import org.eclipse.paho.client.mqttv3.*

class MuistiMqttService {
    private val listeners = mutableListOf<MqttTopicListener<*>>()
    private val client = MuistiMqttClient("tcp://${BuildConfig.MQTT_BASE_URL}")

    private val trigger : (topic: String?, message : String) -> Unit = { topic, message ->
        listeners.forEach {
            if(it.topic == topic)
                it.handleMessage(message)
        }
    }

    init{
        client.connect()
        client.setCallBack(MuistiMqttCallBack(trigger))
        listeners.addAll(UpdatePages.getMqttTopicListeners())
        listeners.addAll(UpdateLayouts.getMqttTopicListeners())
    }
}