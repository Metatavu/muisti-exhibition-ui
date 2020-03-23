package fi.metatavu.muisti.exhibitionui.mqtt

import fi.metatavu.muisti.exhibitionui.BuildConfig
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions

class MuistiMqttClient(serverURL : String) {
    private val mqttClient = MqttClient(serverURL, MqttClient.generateClientId(), null)

    fun connect() {
        val options = MqttConnectOptions()
        options.userName = BuildConfig.MQTT_USER
        options.password = BuildConfig.MQTT_PASSWORD.toCharArray()
        options.keepAliveInterval = 0
        mqttClient.connect(options)
        mqttClient.subscribe("${BuildConfig.MQTT_BASE_TOPIC}/#")
    }

    fun setCallBack(callBack : MuistiMqttCallBack){
        mqttClient.setCallback(callBack)
    }
}
