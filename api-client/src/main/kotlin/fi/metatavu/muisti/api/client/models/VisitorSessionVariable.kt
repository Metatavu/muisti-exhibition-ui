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
package fi.metatavu.muisti.api.client.models


import com.squareup.moshi.Json
/**
 * 
 * @param name 
 * @param value 
 */

data class VisitorSessionVariable (
    @Json(name = "name")
    val name: kotlin.String,
    @Json(name = "value")
    val value: kotlin.String? = null
) 



