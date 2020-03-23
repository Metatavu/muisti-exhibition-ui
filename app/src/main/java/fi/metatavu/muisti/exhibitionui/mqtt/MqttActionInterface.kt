package fi.metatavu.muisti.exhibitionui.mqtt

import fi.metatavu.muisti.exhibitionui.BuildConfig

interface MqttActionInterface  {
    fun getMqttTopicListeners(): List<MqttTopicListener<*>>

    fun mqttTopic(topic: String): String {
        return "${BuildConfig.MQTT_BASE_TOPIC}$topic"
    }
}