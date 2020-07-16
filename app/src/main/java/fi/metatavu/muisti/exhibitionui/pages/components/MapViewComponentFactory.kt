package fi.metatavu.muisti.exhibitionui.pages.components
import com.mapbox.mapboxsdk.maps.MapView
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import mil.nga.geopackage.GeoPackageManager
import java.io.File


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
    }

    override fun onPageDeactivate(pageActivity: PageActivity) {
    }
}