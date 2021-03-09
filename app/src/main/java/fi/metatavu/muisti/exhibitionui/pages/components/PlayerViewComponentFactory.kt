package fi.metatavu.muisti.exhibitionui.pages.components

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Xml
import android.view.View
import android.widget.FrameLayout
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleListener
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
import org.xmlpull.v1.XmlPullParser

/**
 * Component container for player view
 *
 * @param buildContext Component Build Context
 * @param showPlaybackControls whether player controls should be visible
 */
@SuppressLint("ViewConstructor")
class PlayerComponentContainer(
    buildContext: ComponentBuildContext,
    showPlaybackControls: Boolean
): FrameLayout(buildContext.context) {

    val playerView: PlayerView
    val playerControlView: PlayerControlView?

    init {
        val parser: XmlPullParser = ExhibitionUIApplication.instance.resources.getXml(R.xml.video_rotate)
        try {
            parser.next()
            parser.nextTag()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        playerView = PlayerView(context, Xml.asAttributeSet(parser))
        playerView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        addView(playerView)

        if (showPlaybackControls) {
            playerControlView = PlayerControlView(context, Xml.asAttributeSet(parser))
            addView(playerControlView)
        } else {
            playerControlView = null
        }
    }

}

/**
 * Component factory for player components
 */
class PlayerViewComponentFactory : AbstractComponentFactory<PlayerComponentContainer>() {
    override val name: String
        get() = "PlayerView"

    override fun buildComponent(buildContext: ComponentBuildContext): PlayerComponentContainer {
        val showPlaybackControls = getBooleanProperty(buildContext = buildContext, propertyName = "showPlaybackControls") ?: false
        val autoPlay = getBooleanProperty(buildContext = buildContext, propertyName = "autoPlay") ?: true

        val context = buildContext.context
        val parent = buildContext.parents.lastOrNull()

        val view = PlayerComponentContainer(
            buildContext = buildContext,
            showPlaybackControls = showPlaybackControls
        )

        setupView(buildContext, view)
        view.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, view, it)
        }

        val offlineFile = getResourceOfflineFile(buildContext, "src")
        if (offlineFile != null) {
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExhibitionUIApplication"))
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(offlineFile))

            buildContext.addLifecycleListener(PlayerPageViewLifecycleListener(
                videoSource = videoSource,
                view = view,
                autoPlay = autoPlay
            ))
        }

        return view
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: PlayerComponentContainer, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "autoPlay" -> {}
                "showPlaybackControls" -> {}
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
 * @property videoSource video source
 * @property view player component container
 * @property autoPlay whether player should start automatically
 */
private class PlayerPageViewLifecycleListener(
    val videoSource: MediaSource,
    val view: PlayerComponentContainer,
    val autoPlay: Boolean
): PageViewLifecycleListener {

    override fun onPageActivate(activity: MuistiActivity) {
        val context: Context = activity

        val player = SimpleExoPlayer.Builder(context).build()
        player.playWhenReady = autoPlay
        player.prepare(videoSource)
        player.repeatMode = Player.REPEAT_MODE_ALL

        val playerView = view.playerView
        val playerControlView = view.playerControlView

        if (playerControlView != null) {
            playerView.controllerAutoShow = true
            playerControlView.player = player
        } else {
            playerView.useController = false
        }

        playerView.player = player
    }

    override fun onPageDeactivate(activity: MuistiActivity) {
        view.playerView.player?.release()
    }

    override fun onLowMemory() {
        //No need to implement currently
    }

    override fun onResume() {
        //No need to implement currently
    }

    override fun onPause() {
        //No need to implement currently
    }

    override fun onStop() {
        //No need to implement currently
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //No need to implement currently
    }

    override fun onDestroy() {
        //No need to implement currently
    }

}