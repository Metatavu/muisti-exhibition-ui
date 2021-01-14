package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageView
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.android.synthetic.main.activity_page.settings_button
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Main activity class
 */
class MainActivity : MuistiActivity() {

    private var mViewModel: MainViewModel? = null


    private var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        GlobalScope.launch {
            val idlePage = when (val pageId = mViewModel?.getIdlePageId()) {
                null -> null
                else -> PageViewContainer.getPageView(pageId)
            }

            runOnUiThread {
                if (idlePage != null) {
                    setContentView(R.layout.activity_page)
                    openIdlePage(idlePage)
                } else {
                    setContentView(R.layout.activity_main)
                }
                supportActionBar?.hide()
                listenSettingsButton(settings_button)
                listenLoginButton(login_button)
                waitForForcedPortraitMode()
            }
        }
    }

    override fun onDestroy() {
        removeSettingsAndIndexListeners()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        visitorLogin()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        ExhibitionUIApplication.instance.readApiValues()
    }

    private fun openIdlePage(idlePage: PageView) {
        releaseView(idlePage.view)
        runOnUiThread {
            this.root.addView(idlePage.view)
        }
    }

    /**
     * Logs the visitor in to the system if visitor and redirects user forward
     *
     * if user cannot be logged in because of configuration errors, user is redirected into
     * the settings activity
     */
    private fun visitorLogin() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()
        val deviceId = DeviceSettings.getExhibitionDeviceId()

        if (exhibitionId != null && deviceId != null) {
            waitForVisitor {
                visitorSession -> GlobalScope.launch {
                    val language = visitorSession.language
                    val frontPage = mViewModel?.getFrontPage(language = language)

                    if (frontPage != null) {
                        val visitor = VisitorSessionContainer.getCurrentVisitors().getOrNull(0)?.email ?: "vieras"

                        waitForPage(frontPage)
                    } else {
                        startPreviewActivity()
                    }
                }
            }
        } else {
            startSettingsActivity()
        }
    }

    /**
     * Triggers the callback once visitor has been logged in
     *
     * @param callback callback to trigger on visitor login
     */
    private fun waitForVisitor(callback: (visitorSession: VisitorSession) -> Unit) {
        handler.postDelayed({
            val visitorSession = VisitorSessionContainer.getVisitorSession()
            if (visitorSession == null) {
                waitForVisitor(callback)
            } else {
                callback(visitorSession)
            }
        }, "waitForVisitor", 500)
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
        }, "visitorLogin", 500)
    }

    /**
     * Checks if page is constructed in the PageViewContainer and either navigates to it or keeps waiting
     *
     * @param pageId page id to navigate to once it is ready
     */
    private fun waitForForcedPortraitMode() {
        handler.postDelayed({
            if (ExhibitionUIApplication.instance.forcedPortraitMode == null) {
                waitForForcedPortraitMode()
            } else {
                if(ExhibitionUIApplication.instance.forcedPortraitMode == true) {
                    setForcedPortraitMode()
                }
            }
        }, "waitForForcedPortrait", 500)
    }

    /**
     * Starts a preview activity
     */
    private fun startPreviewActivity() {
        val intent = Intent(this, PreviewActivity::class.java)
        this.startActivity(intent)
    }
}
