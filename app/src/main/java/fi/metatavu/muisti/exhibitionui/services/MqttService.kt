package fi.metatavu.muisti.exhibitionui.services

import android.util.Log
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientProvider
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ThisClassReceiver

object MqttService {
    private val mqttClient = MqttClientProvider().getMqttClient()

    private val options = MqttConnectOptions()

    private val listeners = mutableListOf<MqttTopicListener>()

    private val triggerListeners : (String) -> Unit = listener@{ topic ->
        listeners.forEach {
            if(it.topic == topic)
                it.listener()
        }
        return@listener
    }

    fun connect() {
        options.userName = "bqtsuxcu"
        options.password = "Eti3OJ2xuuAC".toCharArray()
        options.keepAliveInterval = 60
        mqttClient?.setCallback(CustomMqttCallback(triggerListeners))
        mqttClient?.connect(options)
        mqttClient?.subscribe("Test")
        Log.d(javaClass.name, "Client is connected: " + mqttClient?.isConnected.toString())

        addListenerToTopic("Test", {Log.d(javaClass.name, "Testi on onnistunut")})
    }

    fun addListenerToTopic(topic: String, listener: () -> Unit) {
        listeners.add(MqttTopicListener(topic, listener))
    }

    fun removeListenerFromTopic(topic: String,listener: () -> Unit) {
        listeners.add(MqttTopicListener(topic, listener))
    }
}

class CustomMqttCallback(trigger: (topic: String) -> Unit) : MqttCallback {
    val listener = trigger

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        log("Arrived message on topic $topic")
        listener("$topic")
    }

    override fun connectionLost(cause: Throwable?) {
        log("ConnectionLost ${cause.toString()}")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    private fun log(text: String) {
        Log.d("MQTT", text)
    }
}

class MqttTopicListener (var topic: String, var listener: () -> Unit)