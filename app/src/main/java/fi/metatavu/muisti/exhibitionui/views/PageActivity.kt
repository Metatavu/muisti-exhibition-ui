package fi.metatavu.muisti.exhibitionui.views

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.TextView
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageView
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.visitors.VisitorSessionContainer
import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt


/**
 * Activity for displaying pages from API
 */
class PageActivity : MuistiActivity() {

    private val showWarningAtSecondsLeft = 5000

    private var logoutWarning : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pageId: String? = intent.getStringExtra("pageId")

        val pageView = PageViewContainer.getPageView(UUID.fromString(pageId))
                ?: // TODO: Handle error
                return

        applyPageTransitions(pageView)

        setContentView(R.layout.activity_page)
        logoutWarning = findViewById(R.id.logout_warning)

        listenSettingsButton(settings_button)
        listenIndexButton(index_page_button)
        setImmersiveMode()

        transitionTime = intent.getLongExtra("pageTransitionTime", 300) + 200L
        this.openView(pageView)
    }

    override fun openView(pageView: PageView) {
        super.openView(pageView)
        triggerVisitorSessionChange(pageView)
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
        countDownTimer = object : CountDownTimer(timeUntilLogout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (logoutWarning == null) {
                    logoutWarning = findViewById(R.id.logout_warning) ?: return
                }

                if (millisUntilFinished < showWarningAtSecondsLeft + 100) {
                    if (logoutWarning?.visibility == View.INVISIBLE) {
                        showViewWithFade(logoutWarning!!)
                    }
                    val secondsLeft = (millisUntilFinished / 1000f).roundToInt()
                    logoutWarning?.text = getString(R.string.logout_warning, secondsLeft.toString())
                }
            }

            override fun onFinish() {
                logoutWarning?.visibility = View.INVISIBLE
            }
        }.start()
    }

    /**
     * Sets the specified view visible with a fade animation
     */
    private fun showViewWithFade(view: View) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate().alpha(1f).setDuration(500).setInterpolator(AccelerateInterpolator())?.start()
    }

    /**
     * Cancels current logout warning
     */
    fun cancelLogoutWarning() {
        countDownTimer?.cancel()
        val logoutWarning = findViewById<TextView>(R.id.logout_warning)
        logoutWarning?.visibility = View.INVISIBLE
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
