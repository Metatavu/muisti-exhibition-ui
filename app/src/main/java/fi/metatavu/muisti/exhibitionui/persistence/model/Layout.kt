package fi.metatavu.muisti.exhibitionui.persistence.model

import androidx.annotation.NonNull
import androidx.room.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.persistence.types.UUIDConverter
import java.util.*

/**
 * Database entity for Layout
 *
 * @property name layout name
 * @property data layout data content
 * @property id layout id
 * @property modifiedAt layout modifiedAt
 */
@Entity (indices = [Index("layoutId", unique = true)])
data class Layout (
    @NonNull
    val name: String,

    @TypeConverters(PageLayoutViewConverter::class)
    val data: PageLayoutView,

    @NonNull
    @TypeConverters(UUIDConverter::class)
    val layoutId: UUID,

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
class PageLayoutViewConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<PageLayoutView> = moshi.adapter<PageLayoutView>(
        PageLayoutView::class.java)

    /**
     * Returns a page layout
     *
     * @param data json data of the Page Layout View
     * @return Page layout view object
     */
    @TypeConverter
    fun stringToPageLayoutView(data: String): PageLayoutView? {
        return jsonAdapter.fromJson(data)
    }

    /**
     * Returns a json page layout
     *
     * @param pageLayoutView page layout view to convert to json
     * @return json data of the Page Layout View
     */
    @TypeConverter
    fun pageLayoutViewToJson(pageLayoutView: PageLayoutView): String {
        return jsonAdapter.toJson(pageLayoutView)
    }
}