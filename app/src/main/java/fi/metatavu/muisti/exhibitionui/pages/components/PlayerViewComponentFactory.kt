package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.pages.PageViewActivator
import fi.metatavu.muisti.exhibitionui.views.PageActivity


/**
 * Component factory for player components
 */
class PlayerViewComponentFactory : AbstractComponentFactory<PlayerView>() {
    override val name: String
        get() = "PlayerView"

    override fun buildComponent(context: Context, parents: Array<View>, pageLayoutView: PageLayoutView, resources: Array<ExhibitionPageResource>, activators: MutableList<PageViewActivator>): PlayerView {
        val playerView = PlayerView(context)
        setId(playerView, pageLayoutView)

        val parent = parents.lastOrNull()
        playerView.layoutParams = getInitialLayoutParams(parent)

        pageLayoutView.properties.forEach {
            this.setProperty(parent, playerView, it)
        }

        activators.add { activate(it, playerView, pageLayoutView, resources) }

        return playerView
    }

    /**
     * Activates the player
     *
     * @param pageActivity page activity
     * @param playerView player view component
     * @param pageLayoutView page layout view
     * @param resources resources
     */
    private fun activate(pageActivity: PageActivity, playerView: PlayerView, pageLayoutView: PageLayoutView, resources: Array<ExhibitionPageResource>) {
        val srcValue = pageLayoutView.properties.firstOrNull { it.name == "src" }?.value
        val offlineFile = getResourceOfflineFile(resources, srcValue)
        offlineFile?: return

        val context: Context = pageActivity
        val player = SimpleExoPlayer.Builder(context).build()
        player.playWhenReady = true
        playerView.player = player
        playerView.useController = false

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExhibitionUIApplication"))
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(offlineFile))
        player.prepare(videoSource)
    }

    /**
     * Sets view player property
     *
     * @param parent parent component
     * @param playerView player view component
     * @param property property
     */
    private fun setProperty(parent: View?, playerView: PlayerView, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "layout_width" -> setLayoutWidth(parent, playerView, property)
                "layout_height" -> setLayoutHeight(parent, playerView, property)
                "width" -> playerView.layoutParams.width = property.value.toInt()
                "height" -> playerView.layoutParams.height = property.value.toInt()
                "background" -> playerView.setBackgroundColor(Color.parseColor(property.value))
                "paddingLeft" -> playerView.setPadding(
                    property.value.toInt(),
                    playerView.paddingTop,
                    playerView.paddingRight,
                    playerView.paddingBottom
                )
                "paddingTop" -> playerView.setPadding(
                    playerView.paddingLeft,
                    property.value.toInt(),
                    playerView.paddingRight,
                    playerView.paddingBottom
                )
                "paddingRight" -> playerView.setPadding(
                    playerView.paddingLeft,
                    playerView.paddingTop,
                    property.value.toInt(),
                    playerView.paddingBottom
                )
                "paddingBottom" -> playerView.setPadding(
                    playerView.paddingLeft,
                    playerView.paddingTop,
                    playerView.paddingRight,
                    property.value.toInt()
                )
                "layout_gravity" -> setLayoutGravity(playerView, property.value)
                "layout_marginTop" -> setLayoutMargin(parent, playerView, property)
                "layout_marginBottom" -> setLayoutMargin(parent, playerView, property)
                "layout_marginRight" -> setLayoutMargin(parent, playerView, property)
                "layout_marginLeft" -> setLayoutMargin(parent, playerView, property)
                "src" -> { }
                else -> Log.d(PlayerViewComponentFactory::javaClass.name, "Property ${property.name} not supported")
            }
        } catch (e: Exception) {
            Log.d(PlayerViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

}