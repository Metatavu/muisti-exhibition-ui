package fi.metatavu.muisti.exhibitionui.test.functional.settings

import fi.metatavu.muisti.api.test.builder.Settings

/**
 * Settings implementation for API test builder
 *
 * @author Antti Leppä
 */
class ApiTestSettings() : Settings {

    /**
     * Returns API service base path
     */
    override val apiBasePath: String
        get() = "http://localhost:1234/v1"

    /**
     * Returns API service base path
     */
    override val filesBasePath: String
        get() = "http://localhost:1234/files"

    /**
     * Returns Keycloak host
     */
    override val keycloakHost: String
        get() = "http://test-keycloak:8080/auth"

    /**
     * Returns Keycloak realm
     */
    override val keycloakRealm: String
        get() = "muisti"

    /**
     * Returns Keycloak client id
     */
    override val keycloakClientId: String
        get() = "test"

    /**
     * Returns Keycloak client secret
     */
    override val keycloakClientSecret: String?
        get() = null

    /**
     * Returns Keycloak admin user
     */
    override val keycloakAdminUser: String
        get() = "admin"

    /**
     * Returns Keycloak admin password
     */
    override val keycloakAdminPass: String
        get() = "test"

    /**
     * Returns MQTT server URL
     */
    override val mqttServerUrl: String
        get() = "localhost"

    /**
     * Returns MQTT topic
     */
    override val mqttTopic: String
        get() = "test"

}