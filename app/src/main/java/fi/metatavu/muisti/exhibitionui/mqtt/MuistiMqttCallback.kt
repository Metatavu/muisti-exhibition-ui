package fi.metatavu.muisti.exhibitionui.mqtt

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.infrastructure.UUIDAdapter
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class MuistiMqttCallBack( var listener: (topic: String?,message: String) -> Unit ) : MqttCallback {

    override fun messageArrived(topic: String?, mqttMessage: MqttMessage?) {
       listener(topic, mqttMessage.toString())
    }

    override fun connectionLost(cause: Throwable?) {
        Log.d(javaClass.name,"ConnectionLost ${cause.toString()}")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}
}

class MqttTopicListener <T>(var topic: String, type: Class<T>, var listener: (T) -> Unit ) {
    private val moshi = Moshi.Builder().add(UUIDAdapter()).add(KotlinJsonAdapterFactory()).build()

    private val jsonAdapter: JsonAdapter<T> = moshi.adapter<T>(type)

    /**
     * Recieves the Mqtt message, converts it into the selected object type and passes it on to the listener.
     *
     * @param message to convert
     */
    fun handleMessage(message: String){
        val messageObject = mqttMessageFromString(message)
        listener(messageObject)
    }

    /**
     * Converts a message string into specified type using moshi
     *
     * @param message message to convert
     */
    private fun mqttMessageFromString(message: String) : T {
        val json = jsonAdapter.fromJson(message)

        return json ?: throw error("Error converting from Json")
    }
}