package fi.metatavu.muisti.exhibitionui.views

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Toast
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageView
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.visitors.VisitorSessionContainer
import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Activity for displaying pages from API
 */
class PageActivity : MuistiActivity() {

    private var logoutWarningToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val pageId: String? = intent.getStringExtra("pageId")

        val pageView = PageViewContainer.getPageView(UUID.fromString(pageId))
                ?: // TODO: Handle error
                return

        super.onCreate(savedInstanceState)

        applyPageTransitions(pageView)

        setContentView(R.layout.activity_page)

        listenSettingsButton(settings_button)
        listenIndexButton(index_page_button)
        setImmersiveMode()

        transitionTime = intent.getLongExtra("pageTransitionTime", 300) + 200L
        this.openView(pageView)
    }

    /**
     * Manually triggers a visitor session change
     */
    fun triggerVisitorSessionChange() {
        val pageView = currentPageView
        if (pageView != null) {
            triggerVisitorSessionChange(pageView = pageView)
        }
    }

    /**
     * Displays logout warning with a running timer
     * @param timeUntilLogout time left until logout
     */
    fun logoutWarning(timeUntilLogout: Long) {
        val logoutWarningText = "Olet kirjautumassa ulos "
        val context = this
        countDownTimer = object : CountDownTimer(timeUntilLogout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                logoutWarningToast?.cancel()
                logoutWarningToast = Toast.makeText(context, logoutWarningText + millisUntilFinished / 1000, Toast.LENGTH_SHORT)
                logoutWarningToast?.show()
            }

            override fun onFinish() {
                logoutWarningToast?.cancel()
            }
        }.start()
    }

    /**
     * Cancels current logout warning
     */
    fun cancelLogoutWarning() {
        countDownTimer?.cancel()
        logoutWarningToast?.cancel()
    }

    override fun openView(pageView: PageView) {
        super.openView(pageView)
        triggerVisitorSessionChange(pageView)
    }

    /**
     * Triggers a visitor session change
     *
     * @param pageView page view
     */
    private fun triggerVisitorSessionChange(pageView: PageView) {
        GlobalScope.launch {
            prepareVisitorSessionChange(pageView)

            runOnUiThread {
                performVisitorSessionChange(pageView)
            }
        }
    }

    /**
     * Executes prepare for visitor session change listeners
     *
     * @param pageView page view
     */
    private suspend fun prepareVisitorSessionChange(pageView: PageView) {
        val pageActivity = this
        val visitorSession = VisitorSessionContainer.getVisitorSession()
        if (visitorSession != null) {
            pageView.visitorSessionListeners.forEach {
                it.prepareVisitorSessionChange(
                    pageActivity = pageActivity,
                    visitorSession = visitorSession
                )
            }
        }
    }

    /**
     * Executes perform for visitor session change listeners
     *
     * @param pageView page view
     */
    private fun performVisitorSessionChange(pageView: PageView) {
        val pageActivity = this
        val visitorSession = VisitorSessionContainer.getVisitorSession()
        if (visitorSession != null) {
            pageView.visitorSessionListeners.forEach {
                it.performVisitorSessionChange(
                    pageActivity = pageActivity,
                    visitorSession = visitorSession
                )
            }
        }
    }

    /**
     * Starts listening for index button click
     *
     * @param button button
     */
    private fun listenIndexButton(button: Button) {
        button.setOnClickListener{
            indexButtonClick()
        }
    }
}

/**
 * Keycode Listener class for triggering events with key presses
 *
 * @param keyCode Keycode that triggers the listener
 * @param listener listener to trigger with keycode
 */
class KeyCodeListener(var keyCode: Int, var listener: () -> Unit)
