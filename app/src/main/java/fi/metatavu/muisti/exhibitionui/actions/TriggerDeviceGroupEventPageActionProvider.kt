package fi.metatavu.muisti.exhibitionui.actions

import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.api.client.models.MqttTriggerDeviceGroupEvent
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Page action provider for group event triggers
 *
 * Action triggers an group event notification via MQTT channel into all group devices
 *
 * @constructor constructor
 * @param properties event properties
 */
class TriggerDeviceGroupEventPageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    override fun performAction(activity: MuistiActivity) {
        val eventName = getPropertyString("name") ?: return
        val payload = MqttTriggerDeviceGroupEvent(event = eventName)
        // TODO: Publish to actual device group
        MqttClientController.publish("events/deviceGroup/deviceGroupId", payload)
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.navigate

}