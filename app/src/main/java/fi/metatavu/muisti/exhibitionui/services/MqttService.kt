package fi.metatavu.muisti.exhibitionui.services

import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController

class MuistiMqttService {

    init {
        MqttClientController.addListeners(UpdatePages.getMqttTopicListeners())
        MqttClientController.addListeners(UpdateLayouts.getMqttTopicListeners())
    }
}