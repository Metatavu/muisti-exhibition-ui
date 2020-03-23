package fi.metatavu.muisti.exhibitionui.services

import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import fi.metatavu.muisti.exhibitionui.mqtt.MuistiMqttCallBack
import fi.metatavu.muisti.exhibitionui.mqtt.MuistiMqttClient

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