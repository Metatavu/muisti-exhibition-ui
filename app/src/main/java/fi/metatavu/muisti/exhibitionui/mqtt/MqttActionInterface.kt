package fi.metatavu.muisti.exhibitionui.mqtt

import fi.metatavu.muisti.exhibitionui.BuildConfig

interface MqttActionInterface  {
    fun getMqttTopicListeners(): List<MqttTopicListener<*>>

    /**
     * Returns a mqtt topic string with the base topic attached.
     *
     * @param topic topic to combine with the base topic
     */
    fun mqttTopic(topic: String): String {
        return "${BuildConfig.MQTT_BASE_TOPIC}$topic"
    }
}