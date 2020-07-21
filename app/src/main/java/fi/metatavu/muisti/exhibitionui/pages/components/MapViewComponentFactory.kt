package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.FrameLayout
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
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.PageActivity

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

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: MapComponentContainer, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "src" -> { }
                "sqlFilterQuery" -> { }
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(PlayerViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

}

private class MapViewLifeCycleListener(val buildContext: ComponentBuildContext, val view: MapComponentContainer): PageViewLifecycleListener, OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var mapboxMap: MapboxMap

    override fun onPageActivate(pageActivity: PageActivity) {
        val context: Context = pageActivity
        Mapbox.getInstance(context, BuildConfig.MAP_BOX_ACCESS_TOKEN)

        val mapView = MapView(context)
        mapView.onStart()
        mapView.getMapAsync(this)

        view.addView(mapView)
    }

    override fun onPageDeactivate(pageActivity: PageActivity) {
        mapView.onDestroy()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.OUTDOORS) {
            @Override
            fun onStyleLoaded(@NonNull style: Style) {
                style.addSource(geoJsonSource)
                style.addLayer(
                    FillExtrusionLayer("deaths", "deaths").withProperties(
                        fillExtrusionColor(Color.RED),
                        fillExtrusionOpacity(0.7f),
                        fillExtrusionHeight(get("height"))))
            }

        }
    }
}