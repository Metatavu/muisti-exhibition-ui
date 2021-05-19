package fi.metatavu.muisti.exhibitionui.mqtt

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.infrastructure.UUIDAdapter
import fi.metatavu.muisti.exhibitionui.BuildConfig

/**
 * Controller for MQTT operations
 *
 * @author Antti Leppä
 * @author Antti Leinonen
 */
class MqttClientController {

    companion object {

        private val client = MuistiMqttClient("tcp://${BuildConfig.MQTT_BASE_URL}")
        private val listeners = mutableListOf<MqttTopicListener<*>>()

        private val trigger : (topic: String?, message : String) -> Unit = { topic, message ->
            listeners.forEach {
                if (it.topic == topic)
                    it.handleMessage(message)
            }
        }

        init {
            client.connect()
            client.setCallBack(MuistiMqttCallBack(trigger))
        }

        /**
         * Returns weather or not the client is connected or not
         * @return true if connected false if not
         */
        fun isConnected(): Boolean {
            return client.isConnected()
        }

        /**
         * Publishes given payload as serialied JSON into given topic
         *
         * @param topic topic
         * @param payload payload
         */
        fun publish(topic: String, payload: Any) {
            val moshi = Moshi.Builder().add(UUIDAdapter()).add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(payload.javaClass)
            val message = adapter.toJson(payload)
            client.publish(topic, message)
        }

        /**
         * Adds list of new MQTT topic listeners
         *
         * @param newListeners new listeners
         */
        fun addListeners(newListeners: List<MqttTopicListener<*>>) {
            listeners.addAll(newListeners)
        }

        /**
         * Adds new MQTT topic listener
         *
         * @param newListener new listener
         */
        fun addListener(newListener: MqttTopicListener<*>): MqttTopicListener<*> {
            listeners.add(newListener)
            return newListener
        }

        /**
         * Removes a MQTT topic listener
         *
         * @param removeListener listener to be removed
         */
        fun removeListener(removeListener: MqttTopicListener<*>) {
            listeners.remove(removeListener)
        }

        /**
         * Removes list of MQTT topic listeners
         *
         * @param removeListeners listeners to be removed
         */
        fun removeListeners(removeListeners: List<MqttTopicListener<*>>) {
            removeListeners.forEach(this::removeListener)
        }

    }

}