package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import fi.metatavu.muisti.exhibitionui.visitors.VisitorSessionContainer
import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Main activity class
 */
class MainActivity : MuistiActivity() {

    private var mViewModel: MainViewModel? = null
    private var handler: Handler = Handler()

    private val visitorSessionObserver = Observer<VisitorSession?> {
        onVisitorSessionChange(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        VisitorSessionContainer.getLiveVisitorSession().observe(this, visitorSessionObserver)

        GlobalScope.launch {
            val exhibitionId = DeviceSettings.getExhibitionId()
            val deviceId = DeviceSettings.getExhibitionDeviceId()

            if (exhibitionId == null || deviceId == null) {
                startSettingsActivity()
            } else {
                val idlePage = when (val pageId = mViewModel?.getIdlePageId()) {
                    null -> null
                    else -> PageViewContainer.getPageView(pageId)
                }

                runOnUiThread {
                    if (idlePage != null) {
                        setContentView(R.layout.activity_page)
                        openView(idlePage)
                    } else {
                        setContentView(R.layout.activity_main)
                    }

                    setImmersiveMode()
                    listenLoginButton(login_button)
                    listenSettingsButton(settings_button)
                    waitForForcedPortraitMode(idlePage?.orientation)
                }
            }
        }
    }

    override fun onDestroy() {
        VisitorSessionContainer.getLiveVisitorSession().removeObserver(visitorSessionObserver)
        removeSettingsAndIndexListeners()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        ExhibitionUIApplication.instance.readApiValues()
    }

    private suspend fun startVisitorSession(visitorSession: VisitorSession) {
        val language = visitorSession.language
        val frontPage = mViewModel?.getFrontPage(
            language = language,
            visitorSession = visitorSession
        )

        if (frontPage != null) {
            waitForPage(frontPage)
        } else {
            startPreviewActivity()
        }
    }

    /**
     * Checks if page is constructed in the PageViewContainer and either navigates to it or keeps waiting
     *
     * @param pageId page id to navigate to once it is ready
     */
    private fun waitForPage(pageId: UUID) {
        handler.postDelayed({
            if (PageViewContainer.contains(pageId)) {
                goToPage(pageId)
            } else {
                waitForPage(pageId)
            }
        }, 500)
    }

    /**
     * Checks if forced portrait mode setting is loaded,
     * if true forcces portrait otherwise sets the specified orientation.
     *
     * @param orientation orientation to set if portrait mode is not forced.
     */
    private fun waitForForcedPortraitMode(orientation: Int?) {
        handler.postDelayed({
            if (ExhibitionUIApplication.instance.forcedPortraitMode == null) {
                waitForForcedPortraitMode(orientation)
            } else {
                if(ExhibitionUIApplication.instance.forcedPortraitMode == true) {
                    setForcedPortraitMode()
                } else {
                    requestedOrientation = orientation ?: return@postDelayed
                }
            }
        }, 500)
    }

    /**
     * Starts a preview activity
     */
    private fun startPreviewActivity() {
        val intent = Intent(this, PreviewActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Handler for visitor session changes
     *
     * @param visitorSession visitor session
     */
    private fun onVisitorSessionChange(visitorSession: VisitorSession?) {
        visitorSession ?: return

        GlobalScope.launch {
            startVisitorSession(visitorSession)
        }
    }

}
