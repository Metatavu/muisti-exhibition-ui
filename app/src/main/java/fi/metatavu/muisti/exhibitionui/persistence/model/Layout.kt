package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.annotation.NonNull
import androidx.room.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutView

/**
 * Database entity for Layout
 *
 * @property name layout name
 * @property data layout data content
 * @property id layout id
 * @property exhibitionId layout exhibitionId
 * @property modifiedAt layout modifiedAt
 */
@Entity (indices = [Index("layoutId", unique = true)])
data class Layout (
    @NonNull
    val name: String,

    @TypeConverters(ExhibitionPageLayoutViewConverter::class)
    val data: ExhibitionPageLayoutView,

    @NonNull
    val layoutId: String,

    @NonNull
    val exhibitionId: String,

    @NonNull
    val modifiedAt: String

) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}

/**
 * Converter class for Exhibition Page Layout Views
 *
 */
class ExhibitionPageLayoutViewConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<ExhibitionPageLayoutView> = moshi.adapter<ExhibitionPageLayoutView>(
        ExhibitionPageLayoutView::class.java)

    /**
     * Returns a exhibition page layout
     *
     * @param data json data of the Exhibition Page Layout View
     * @return Exhibition page layout view object
     */
    @TypeConverter
    fun stringToExhibitionPageLayoutView(data: String): ExhibitionPageLayoutView? {
        return jsonAdapter.fromJson(data)
    }

    /**
     * Returns a json exhibition page layout
     *
     * @param exhibitionPageLayoutView page layout view to convert to json
     * @return json data of the Exhibition Page Layout View
     */
    @TypeConverter
    fun exhibitionPageLayoutViewToJson(exhibitionPageLayoutView: ExhibitionPageLayoutView): String {
        return jsonAdapter.toJson(exhibitionPageLayoutView)
    }
}