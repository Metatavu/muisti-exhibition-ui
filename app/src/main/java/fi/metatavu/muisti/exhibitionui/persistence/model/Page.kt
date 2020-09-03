package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.annotation.NonNull
import androidx.room.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.infrastructure.UUIDAdapter
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventTrigger
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.ExhibitionPageTransition
import fi.metatavu.muisti.api.client.models.Transition
import fi.metatavu.muisti.exhibitionui.persistence.types.UUIDConverter
import java.util.*

/**
 * Database entity for page
 *
 */
@Entity
data class Page (

    @PrimaryKey
    @NonNull
    @TypeConverters(UUIDConverter::class)
    val pageId: UUID,

    @NonNull
    val name: String,

    @NonNull
    val language: String,

    @NonNull
    var orderNumber: Int,

    @NonNull
    @TypeConverters(UUIDConverter::class)
    val layoutId: UUID,

    @NonNull
    @TypeConverters(UUIDConverter::class)
    val exhibitionId: UUID,

    @NonNull
    val modifiedAt: String,

    @NonNull
    @TypeConverters(ExhibitionPageViewConverter::class)
    val resources: Array<ExhibitionPageResource> = emptyArray(),

    @NonNull
    @TypeConverters(ExhibitionPageViewConverter::class)
    val eventTriggers: Array<ExhibitionPageEventTrigger> = emptyArray(),

    @NonNull
    @TypeConverters(ExhibitionPageViewConverter::class)
    val enterTransitions: Array<ExhibitionPageTransition> = emptyArray(),

    @NonNull
    @TypeConverters(ExhibitionPageViewConverter::class)
    val exitTransitions: Array<ExhibitionPageTransition> = emptyArray()
)

/**
 * Converter class for Exhibition Page
 *
 */
class ExhibitionPageViewConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(UUIDAdapter())
        .build()

    private val pageResourceJsonAdapter: JsonAdapter<Array<ExhibitionPageResource>> = moshi.adapter<Array<ExhibitionPageResource>>(
        Array<ExhibitionPageResource>::class.java)

    private val pageEventJsonAdapter: JsonAdapter<Array<ExhibitionPageEventTrigger>> = moshi.adapter<Array<ExhibitionPageEventTrigger>>(
        Array<ExhibitionPageEventTrigger>::class.java)

    private val transitionJsonAdapter: JsonAdapter<Array<ExhibitionPageTransition>> = moshi.adapter<Array<ExhibitionPageTransition>>(
        Array<ExhibitionPageTransition>::class.java)

    /**
     * Returns an exhibition page resource
     *
     * @param data json data of the Exhibition Page Resources
     * @return Exhibition page resource object
     */
    @TypeConverter
    fun stringToExhibitionPageResource(data: String): Array<ExhibitionPageResource>? {
        return pageResourceJsonAdapter.fromJson(data)
    }

    /**
     * Returns a json exhibition page resource
     *
     * @param exhibitionPageResource page resource to convert to json
     * @return json data of the Exhibition Page Resource
     */
    @TypeConverter
    fun exhibitionPageResourceToJson(exhibitionPageResource: Array<ExhibitionPageResource>): String {
        return pageResourceJsonAdapter.toJson(exhibitionPageResource)
    }

    /**
     * Returns an array of transitions
     *
     * @param data json data of the transitions
     * @return Transitions object
     */
    @TypeConverter
    fun stringToTransition(data: String): Array<ExhibitionPageTransition>? {
        return transitionJsonAdapter.fromJson(data)
    }

    /**
     * Returns a json transitions
     *
     * @param transitions page transitions to convert to json
     * @return json data of the Transitions
     */
    @TypeConverter
    fun transitionToJson(transitions: Array<ExhibitionPageTransition>): String {
        return transitionJsonAdapter.toJson(transitions)
    }

    /**
     * Returns an exhibition page event trigger
     *
     * @param data json data of the Exhibition Page Event Trigger
     * @return Exhibition page event trigger object
     */
    @TypeConverter
    fun stringToExhibitionPageEventTrigger(data: String): Array<ExhibitionPageEventTrigger>? {
        try {
            return pageEventJsonAdapter.fromJson(data)
        } catch (e: Exception) {
            return arrayOf()
        }
    }

    /**
     * Returns a json exhibition page event trigger
     *
     * @param exhibitionPageEventTrigger page event trigger to convert to json
     * @return json data of the Exhibition Page Event Trigger
     */
    @TypeConverter
    fun exhibitionPageTriggerToJson(exhibitionPageEventTrigger: Array<ExhibitionPageEventTrigger>): String {
        return pageEventJsonAdapter.toJson(exhibitionPageEventTrigger)
    }
}