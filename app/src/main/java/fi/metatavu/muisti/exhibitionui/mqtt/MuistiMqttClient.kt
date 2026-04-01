package fi.metatavu.muisti.exhibitionui.mqtt

import fi.metatavu.muisti.exhibitionui.BuildConfig
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import android.util.Log

class MuistiMqttClient(private val serverURLs: List<String>) {

    private var mqttClient: MqttClient? = null
    private var callback: MuistiMqttCallBack? = null

    /**
     * Connect the client
     */
    @Throws(MqttException::class, IllegalStateException::class)
    fun connect() {
        if (serverURLs.isEmpty()) {
            throw IllegalStateException("No MQTT server URLs configured")
        }

        if (mqttClient?.isConnected == true) {
            return
        }

        var lastException: MqttException? = null

        for (serverURL in serverURLs) {
            Log.e(javaClass.name, "Connectiong to MQTT server ${serverURL}")

            try {
                mqttClient?.let { existingClient ->
                    try {
                        if (existingClient.isConnected) {
                            existingClient.disconnect()
                        }
                    } catch (_: Exception) {
                    }

                    try {
                        existingClient.close()
                    } catch (_: Exception) {
                    }
                }

                val client = MqttClient(serverURL, MqttClient.generateClientId(), null)

                callback?.let { client.setCallback(ReconnectableCallback(it)) }

                val options = MqttConnectOptions().apply {
                    userName = BuildConfig.MQTT_USER
                    password = BuildConfig.MQTT_PASSWORD.toCharArray()
                    keepAliveInterval = 30
                    connectionTimeout = 10
                    isCleanSession = true
                }

                client.connect(options)
                client.subscribe("${BuildConfig.MQTT_BASE_TOPIC}/#")

                mqttClient = client

                Log.d(javaClass.name, "Connected to MQTT at ${serverURL}")
                return
            } catch (e: MqttException) {
                Log.e(javaClass.name, "Could not connect to MQTT at ${serverURL}", e)
                lastException = e
            }
        }

        Log.e(javaClass.name, "Failed to connect to any configured MQTT servers")
    }

    /**
     * Returns whether the client is connected or not
     *
     * @return true if connected, otherwise false
     */
    fun isConnected(): Boolean {
        return mqttClient?.isConnected == true
    }

    /**
     * Publish message into MQTT topic
     *
     * @param topic topic
     * @param message message
     */
    @Throws(IllegalStateException::class, MqttException::class)
    fun publish(topic: String, message: String) {
        val client = mqttClient ?: throw IllegalStateException("MQTT client is not initialized")
        if (!client.isConnected) {
            throw IllegalStateException("MQTT client is not connected")
        }

        client.publish(
            "${BuildConfig.MQTT_BASE_TOPIC}/$topic",
            MqttMessage(message.toByteArray())
        )
    }

    /**
     * Set callback to use when receiving messages.
     *
     * @param callBack MuistiMqttCallBack to trigger when receiving messages.
     */
    fun setCallBack(callBack: MuistiMqttCallBack) {
        callback = callBack
        mqttClient?.setCallback(ReconnectableCallback(callBack))
    }

    /**
     * Disconnect and release resources.
     */
    fun disconnect() {
        mqttClient?.let { client ->
            try {
                if (client.isConnected) {
                    client.disconnect()
                }
            } catch (_: Exception) {
            }

            try {
                client.close()
            } catch (_: Exception) {
            }
        }

        mqttClient = null
    }

    /**
     * Wraps the original callback and tries to reconnect through all configured URLs
     * if the connection is lost.
     */
    private inner class ReconnectableCallback(
        private val delegate: MuistiMqttCallBack
    ) : MqttCallback {

        override fun connectionLost(cause: Throwable?) {
            delegate.connectionLost(cause)

            try {
                connect()
            } catch (_: Exception) {
            }
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            delegate.messageArrived(topic, message)
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            delegate.deliveryComplete(token)
        }
    }
}