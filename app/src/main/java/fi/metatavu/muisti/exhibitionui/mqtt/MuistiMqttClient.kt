package fi.metatavu.muisti.exhibitionui.mqtt

import fi.metatavu.muisti.exhibitionui.BuildConfig
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class MuistiMqttClient(serverURL : String) {

    private val mqttClient = MqttClient(serverURL, MqttClient.generateClientId(), null)

    /**
     * Connect the client
     */
    fun connect() {
        val options = MqttConnectOptions()
        options.userName = BuildConfig.MQTT_USER
        options.password = BuildConfig.MQTT_PASSWORD.toCharArray()
        options.keepAliveInterval = 0
        mqttClient.connect(options)
        mqttClient.subscribe("${BuildConfig.MQTT_BASE_TOPIC}/#")
    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

    /**
     * Publish message into MQTT topic
     *
     * @param topic topic
     * @param message message
     */
    fun publish(topic: String, message: String) {
        mqttClient.publish("${BuildConfig.MQTT_BASE_TOPIC}/$topic", MqttMessage(message.toByteArray()))
    }

    /**
     * Set callback to use when receiving messages.
     *
     * @param callBack MuistiMqttCallBack to trigger when recieving messages.
     */
    fun setCallBack(callBack : MuistiMqttCallBack){
        mqttClient.setCallback(callBack)
    }
}
