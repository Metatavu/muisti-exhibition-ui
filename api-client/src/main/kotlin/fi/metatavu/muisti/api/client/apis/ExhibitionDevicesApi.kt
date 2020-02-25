/**
* Muisti API
* No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
*
* The version of the OpenAPI document: 1.0.0
* 
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package fi.metatavu.muisti.api.client.apis

import fi.metatavu.muisti.api.client.models.Error
import fi.metatavu.muisti.api.client.models.ExhibitionDevice

import fi.metatavu.muisti.api.client.infrastructure.ApiClient
import fi.metatavu.muisti.api.client.infrastructure.ClientException
import fi.metatavu.muisti.api.client.infrastructure.ClientError
import fi.metatavu.muisti.api.client.infrastructure.ServerException
import fi.metatavu.muisti.api.client.infrastructure.ServerError
import fi.metatavu.muisti.api.client.infrastructure.MultiValueMap
import fi.metatavu.muisti.api.client.infrastructure.RequestConfig
import fi.metatavu.muisti.api.client.infrastructure.RequestMethod
import fi.metatavu.muisti.api.client.infrastructure.ResponseType
import fi.metatavu.muisti.api.client.infrastructure.Success
import fi.metatavu.muisti.api.client.infrastructure.toMultiValue

class ExhibitionDevicesApi(basePath: kotlin.String = "http://localhost") : ApiClient(basePath) {

    /**
    * Create a device
    * Creates new exhibition device
    * @param exhibitionId exhibition id 
    * @param exhibitionDevice Payload 
    * @return ExhibitionDevice
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun createExhibitionDevice(exhibitionId: java.util.UUID, exhibitionDevice: ExhibitionDevice) : ExhibitionDevice {
        val localVariableBody: kotlin.Any? = exhibitionDevice
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/exhibitions/{exhibitionId}/devices".replace("{"+"exhibitionId"+"}", "$exhibitionId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<ExhibitionDevice>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as ExhibitionDevice
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> throw ClientException((localVarResponse as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((localVarResponse as ServerError<*>).message ?: "Server error")
        }
    }

    /**
    * Deletes device.
    * Delets exhibition device.
    * @param exhibitionId exhibition id 
    * @param deviceId device id 
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun deleteExhibitionDevice(exhibitionId: java.util.UUID, deviceId: java.util.UUID) : Unit {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.DELETE,
            "/exhibitions/{exhibitionId}/devices/{deviceId}".replace("{"+"exhibitionId"+"}", "$exhibitionId").replace("{"+"deviceId"+"}", "$deviceId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<Any?>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> throw ClientException((localVarResponse as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((localVarResponse as ServerError<*>).message ?: "Server error")
        }
    }

    /**
    * Find a device
    * Finds a device by id
    * @param exhibitionId exhibition id 
    * @param deviceId device id 
    * @return ExhibitionDevice
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun findExhibitionDevice(exhibitionId: java.util.UUID, deviceId: java.util.UUID) : ExhibitionDevice {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/exhibitions/{exhibitionId}/devices/{deviceId}".replace("{"+"exhibitionId"+"}", "$exhibitionId").replace("{"+"deviceId"+"}", "$deviceId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<ExhibitionDevice>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as ExhibitionDevice
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> throw ClientException((localVarResponse as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((localVarResponse as ServerError<*>).message ?: "Server error")
        }
    }

    /**
    * List devices
    * List exhibition devices
    * @param exhibitionId Exhibition id 
    * @param exhibitionGroupId Filter by exhibition group id (optional)
    * @return kotlin.Array<ExhibitionDevice>
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun listExhibitionDevices(exhibitionId: java.util.UUID, exhibitionGroupId: java.util.UUID?) : kotlin.Array<ExhibitionDevice> {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (exhibitionGroupId != null) {
                    put("exhibitionGroupId", listOf(exhibitionGroupId.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/exhibitions/{exhibitionId}/devices".replace("{"+"exhibitionId"+"}", "$exhibitionId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<kotlin.Array<ExhibitionDevice>>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.Array<ExhibitionDevice>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> throw ClientException((localVarResponse as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((localVarResponse as ServerError<*>).message ?: "Server error")
        }
    }

    /**
    * Updates device.
    * Updates device.
    * @param exhibitionId exhibition id 
    * @param deviceId device id 
    * @param exhibitionDevice Payload 
    * @return ExhibitionDevice
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun updateExhibitionDevice(exhibitionId: java.util.UUID, deviceId: java.util.UUID, exhibitionDevice: ExhibitionDevice) : ExhibitionDevice {
        val localVariableBody: kotlin.Any? = exhibitionDevice
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.PUT,
            "/exhibitions/{exhibitionId}/devices/{deviceId}".replace("{"+"exhibitionId"+"}", "$exhibitionId").replace("{"+"deviceId"+"}", "$deviceId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<ExhibitionDevice>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as ExhibitionDevice
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> throw ClientException((localVarResponse as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((localVarResponse as ServerError<*>).message ?: "Server error")
        }
    }

}
