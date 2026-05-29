package fi.metatavu.muisti.exhibitionui.actions

import android.util.Log
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.api.client.models.MqttTriggerDeviceGroupEvent
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity

/**
 * Page action provider for group event triggers
 *
 * Action triggers an group event notification via MQTT channel into all group devices
 *
 * @constructor constructor
 * @param properties event properties
 */
class TriggerDeviceGroupEventPageActionProvider(properties: Array<ExhibitionPageEventProperty>) : AbstractPageActionProvider(properties) {

    override fun performAction(activity: MuistiActivity) {

        val eventName = getPropertyString("name") ?: return
        val payload = MqttTriggerDeviceGroupEvent(event = eventName)

        val deviceGroupId = ExhibitionUIApplication.instance.deviceGroupId
        if (deviceGroupId != null) {
            Log.d(javaClass.name, "Triggering device group event $eventName to device group $deviceGroupId")
            MqttClientController.publish("events/deviceGroup/$deviceGroupId", payload)
            Log.d(javaClass.name, "Triggered device group event $eventName to device group $deviceGroupId")
        } else {
            Log.w(javaClass.name, "Device group id not set, cannot trigger device group event")
        }
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.triggerdevicegroupevent
}