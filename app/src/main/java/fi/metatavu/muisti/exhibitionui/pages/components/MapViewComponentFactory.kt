package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Component container for initializing life cycle-listener for Map View
 * @param buildContext Component Build Context
 */
class MapComponentContainer(buildContext: ComponentBuildContext): FrameLayout(buildContext.context) {

    init {
        buildContext.addLifecycleListener(
            MapViewLifeCycleListener(buildContext,this)
        )
    }

}

/**
 * The component factory for MapBox MapView-components
 */
class MapViewComponentFactory: AbstractComponentFactory<MapComponentContainer>() {
    override val name: String
        get() = "MapView"

    override fun buildComponent(buildContext: ComponentBuildContext): MapComponentContainer {
        val componentContainer = MapComponentContainer(buildContext)
        setId(componentContainer, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        componentContainer.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, componentContainer, it)
        }
        return componentContainer
    }
}

/**
 * Life cycle-listener for Map View
 *
 * @param buildContext buildContext
 * @param view mapComponentContainer
 *
 */
private class MapViewLifeCycleListener(val buildContext: ComponentBuildContext, val view: MapComponentContainer): PageViewLifecycleListener, OnMapReadyCallback {

    private var mapView: MapView? = null
    private var geoJsonSource: GeoJsonSource? = null
    private var mapboxMap: MapboxMap? = null

    override fun onPageActivate(pageActivity: PageActivity) {
        val context: Context = pageActivity
        Mapbox.getInstance(context, BuildConfig.MAP_BOX_ACCESS_TOKEN)

        val mapView = MapView(context)
        mapView.onStart()
        mapView.getMapAsync(this)

        view.addView(mapView)
    }

    override fun onPageDeactivate(pageActivity: PageActivity) {
        mapView?.onStop()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap?.setStyle(Style.OUTDOORS) {
            @Override
            fun onStyleLoaded(@NonNull style: Style) {
                //Implement once the Geopackage-library is functional
            }

        }
    }

    override fun onLowMemory() {
        mapView?.onLowMemory()
    }

    override fun onPause() {
        mapView?.onPause()
    }

    override fun onResume() {
        mapView?.onResume()
    }

    override fun onStop() {
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        mapView?.onDestroy()
    }

}
