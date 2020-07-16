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
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import java.io.File

/**
 * Component factory for player components
 */§
class PlayerViewComponentFactory : AbstractComponentFactory<PlayerView>() {
    override val name: String
        get() = "PlayerView"

    override fun buildComponent(buildContext: ComponentBuildContext): PlayerView {
        val playerView = PlayerView(buildContext.context)
        setId(playerView, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        playerView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, playerView, it)
        }

        val offlineFile = getResourceOfflineFile(buildContext, "src")
        if (offlineFile != null) {
            buildContext.addLifecycleListener(PlayerPageViewLifecycleListener(offlineFile, playerView))
        }

        return playerView
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: PlayerView, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "src" -> { }
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(PlayerViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

}

/**
 * Lifecycle listener for player page view component.
 *
 * Listener class is responsible of
 *
 * @property offlineFile offlined video file
 * @property playerView player view
 */
private class PlayerPageViewLifecycleListener(val offlineFile: File, val playerView: PlayerView): PageViewLifecycleListener {

    override fun onPageActivate(pageActivity: PageActivity) {
        val context: Context = pageActivity
        val player = SimpleExoPlayer.Builder(context).build()
        player.playWhenReady = true
        playerView.player = player
        playerView.useController = false

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExhibitionUIApplication"))
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(offlineFile))
        player.prepare(videoSource)
    }

    override fun onPageDeactivate(pageActivity: PageActivity) {
        playerView.player?.release()
    }

}