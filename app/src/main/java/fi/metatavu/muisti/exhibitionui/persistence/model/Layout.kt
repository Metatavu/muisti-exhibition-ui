package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutView

/**
 * Database entity for Layout
 *
 * @property name layout name
 * @property data layout data content
 * @property id layout id
 * @property exhibitionId layout exhibitionId
 * @property creatorId layout creatorId
 * @property lastModifierId layout lastModifier user Id
 * @property createdAt layout createdAt
 * @property modifiedAt layout modifiedAt
 */
@Entity (indices = [Index("layoutId", unique = true)])
data class Layout (

    val name: String,

    @TypeConverters(ExhibitionPageLayoutViewConverter::class)
    val data: ExhibitionPageLayoutView,

    val layoutId: String,

    val exhibitionId: String? = null,

    val creatorId: String? = null,

    val lastModifierId: String? = null,

    val createdAt: String? = null,

    val modifiedAt: String? = null

) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}

class ExhibitionPageLayoutViewConverter {
    private var gson = Gson()

    @TypeConverter
    fun stringToExhibitionPageLayoutView(data: String?): ExhibitionPageLayoutView {
        val listType = object : TypeToken<ExhibitionPageLayoutView>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun exhibitionPageLayoutViewToJson(someObjects: ExhibitionPageLayoutView): String {
        return gson.toJson(someObjects)
    }
}