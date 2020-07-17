package fi.metatavu.muisti.exhibitionui.pages.components
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.db.GeoPackageConnection
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

private class MapViewLifeCycleListener(val view: MapView, val offLineFile: File, val sqlFilterQuery: String): PageViewLifecycleListener {

    override fun onPageActivate(pageActivity: PageActivity) {
        val geoPackage = GeoPackageManager.open(offLineFile)
        val results = geoPackage.connection.query(sqlFilterQuery, ArrayList<String>().toTypedArray())
        val geoJsonSource = MapViewUtils().constructGeoJson(results, geoPackage)


    }

    override fun onPageDeactivate(pageActivity: PageActivity) {
    }
}

private class MapViewUtils() {
    fun constructGeoJson(dbResults: ResultSet, geoPackage: GeoPackage): GeoJsonSource {
        val featureCollection = JSONObject()
        featureCollection.put("type", "FeatureCollection")
        while(dbResults.next()) {
            val deathPlaceId = dbResults.getInt("deathPlaceId")

            if (deathPlaceId != null) {
                val feature = JSONObject()
                feature.put("type", "Feature")
                val geometry = JSONObject()
                val coordinates = JSONArray()

                val point = geoPackage.getFeatureDao("locations").query("id == $deathPlaceId").geometry.geometry.centroid

                //Finish geometry
            }
        }

        return GeoJsonSource("deaths", featureCollection.toString())
    }
}