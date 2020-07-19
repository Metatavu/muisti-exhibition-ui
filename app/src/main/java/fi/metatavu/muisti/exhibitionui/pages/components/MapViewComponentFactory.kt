package fi.metatavu.muisti.exhibitionui.pages.components
import android.content.Context
import android.graphics.Color
import androidx.annotation.NonNull
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.get
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.sf.Point
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.sql.ResultSet

/**
 * The component factory for MapBox MapView-components
 */
class MapViewComponentFactory: AbstractComponentFactory<MapView>() {
    override val name: String
        get() = "MapView"

    override fun buildComponent(buildContext: ComponentBuildContext): MapView {
        val mapView = MapView(buildContext.context)
        setId(mapView, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        mapView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, mapView, it)
        }

        val offlineFile = getResourceOfflineFile(buildContext, "src")
        val sqlFilterQuery = getResourceData(buildContext, "sqlFilterQuery")
        if (offlineFile != null && sqlFilterQuery != null) {
            buildContext.addLifecycleListener(MapViewLifeCycleListener(mapView, offlineFile, sqlFilterQuery))
        }

        return mapView
    }

}

private class MapViewLifeCycleListener(val view: MapView, val offLineFile: File, val sqlFilterQuery: String): PageViewLifecycleListener, OnMapReadyCallback {

    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var mapboxMap: MapboxMap
    private val mapViewUtils = MapViewUtils()

    override fun onPageActivate(pageActivity: PageActivity) {
        val geoPackage = GeoPackageManager.open(offLineFile)
        val results = geoPackage.connection.query(sqlFilterQuery, ArrayList<String>().toTypedArray())
        geoJsonSource = mapViewUtils.constructGeoJson(results, geoPackage)

        val context: Context = pageActivity
        Mapbox.getInstance(context, "pk.eyJ1Ijoic2ltZW9ucGFsdG9ub3YiLCJhIjoiY2thMmM3cnhzMDAzODNlbDlhc3IwZzJ1MSJ9.5AA8AXZW9g9wAtj2V5hcvA")
        view.getMapAsync(this)
    }

    override fun onPageDeactivate(pageActivity: PageActivity) {}

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.OUTDOORS) {
            @Override
            fun onStyleLoaded(@NonNull style: Style) {
                style.addSource(geoJsonSource);
                style.addLayer(
                    FillExtrusionLayer("deaths", "deaths").withProperties(
                        fillExtrusionColor(Color.RED),
                        fillExtrusionOpacity(0.7f),
                        fillExtrusionHeight(get("height"))));
            }

        }
    }
}

private class MapViewUtils() {
    fun constructGeoJson(dbResults: ResultSet, geoPackage: GeoPackage): GeoJsonSource {
        val locationDeathMap = HashMap<Point, Int>()
        while(dbResults.next()) {
            val deathPlaceId = dbResults.getInt("deathPlaceId")
            if (deathPlaceId != null) {
                val point = geoPackage.getFeatureDao("locations").query("id == $deathPlaceId").geometry.geometry.centroid
                var foundExistingEntry = false

                locationDeathMap.forEach { deathLocationEntry ->
                    if (deathLocationEntry.key.x == point.x && deathLocationEntry.key.y == point.y) {
                        val amount = locationDeathMap[deathLocationEntry.key]
                        locationDeathMap[deathLocationEntry.key] = amount!! + 1
                        foundExistingEntry = true
                    }
                }

                if (!foundExistingEntry) {
                    locationDeathMap[point] = 1
                }
            }
        }

        val featureCollection = JSONObject()
        featureCollection.put("type", "FeatureCollection")
        val features = JSONArray()

        locationDeathMap.forEach { deathLocationEntry ->
            val feature = JSONObject()
            feature.put("type", "Feature")
            val geometry = JSONObject()
            val coordinates = JSONArray()

            val latitude = deathLocationEntry.key.x.toString()
            val longitude = deathLocationEntry.key.y.toString()

            val p1 = JSONArray()
            p1.put(java.lang.Float.valueOf(longitude))
            p1.put(java.lang.Float.valueOf(latitude))
            coordinates.put(p1)
            val p2 = JSONArray()
            p2.put(java.lang.Float.valueOf(longitude) + 1.toDouble())
            p2.put(java.lang.Float.valueOf(latitude))
            coordinates.put(p2)
            val p3 = JSONArray()
            p3.put(java.lang.Float.valueOf(longitude) + 1.toDouble())
            p3.put(java.lang.Float.valueOf(latitude) + 1.toDouble())
            coordinates.put(p3)
            val p4 = JSONArray()
            p4.put(java.lang.Float.valueOf(longitude))
            p4.put(java.lang.Float.valueOf(latitude) + 1.toDouble())
            coordinates.put(p4)
            val p5 = JSONArray()
            p5.put(java.lang.Float.valueOf(longitude))
            p5.put(java.lang.Float.valueOf(latitude))
            coordinates.put(p5)

            geometry.put("type", "Polygon")
            val arr = JSONArray()
            arr.put(coordinates)
            geometry.put("coordinates", arr)

            feature.put("geometry", geometry)

            val properties = JSONObject()
            val height: Int = deathLocationEntry.value * 100
            properties.put("height", height)
            feature.put("properties", properties)
            features.put(feature)
        }

        return GeoJsonSource("deaths", featureCollection.toString())
    }
}