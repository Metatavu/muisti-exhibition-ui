package fi.metatavu.muisti.exhibitionui.views

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Pair
import android.widget.Toast
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.pages.PageView
import kotlinx.android.synthetic.main.activity_page.*
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import java.util.*
import kotlin.math.max
import android.os.CountDownTimer
import android.view.MotionEvent
import com.github.rongi.rotate_layout.layout.RotateLayout
import fi.metatavu.muisti.exhibitionui.R


/**
 * Muisti activity abstract class
 */
abstract class MuistiActivity : AppCompatActivity() {

    private var settingsClickCounter = 0
    private var logoutWarningToast: Toast? = null
    private val clickCounterHandler = Handler()
    protected var currentPageView: PageView? = null
    val transitionElements: MutableList<View> = mutableListOf()
    var countDownTimer: CountDownTimer? = null


    override fun onResume() {
        super.onResume()
        if(ExhibitionUIApplication.instance.forcedPortraitMode == true){
            setForcedPortraitMode()
        }
    }

    /**
     * Starts listening for index button click
     *
     * @param button button
     */
    protected fun listenIndexButton(button: Button) {
        button.setOnClickListener{
            indexButtonClick()
        }
    }

    /**
     * Starts listening for settings button click
     *
     * @param button button
     */
    protected fun listenSettingsButton(button: Button) {
        button.setOnClickListener{
            settingsButtonClick()
        }
    }

    /**
     * Starts listening for login button click
     *
     * @param button button
     */
    protected fun listenLoginButton(button: Button) {
        button.setOnClickListener{
            loginButtonClick()
        }
    }

    /**
     * Removes settings and index page listeners
     */
    protected fun removeSettingsAndIndexListeners() {
        clickCounterHandler.removeCallbacksAndMessages(null)
    }

    /**
     * Navigates to the page with transitions
     *
     * @param pageId page id
     * @param sharedElements shared elements to morph during transition or null
     */
    fun goToPage(pageId: UUID, sharedElements: List<View>? = null) {
        val intent = Intent(this, PageActivity::class.java).apply{
            putExtra("pageId", pageId.toString())
        }

        if (!sharedElements.isNullOrEmpty()) {
            val transitionElementPairs = sharedElements.map { Pair.create(it, it.transitionName ?: "") }.toTypedArray()
            val targetElements = transitionElementPairs.map { it.second }
            intent.apply { putStringArrayListExtra("elements", ArrayList(targetElements))}
            val options = ActivityOptions
                .makeSceneTransitionAnimation(this, *transitionElementPairs)
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        val finishTimeout = max(window.exitTransition?.duration ?: 0, window.enterTransition?.duration  ?: 0)
        clickCounterHandler.postDelayed({
            finish()
        }, max(finishTimeout, 2000))
    }

    /**
     * Finds a view with tag
     *
     * @param tag tag
     * @return view or null if not found
     */
    protected fun findViewWithTag(tag: String?): View? {
        tag ?: return null
        return root.findViewWithTag(tag)
    }

    /**
     * Sets current activity into application
     *
     * @param activity activity
     */
    protected fun setCurrentActivity(activity: PageActivity?) {
        ExhibitionUIApplication.instance.setCurrentActivity(activity)
    }

    /**
     * Gets the current activity
     *
     * @return activity or null
     */
    protected fun getCurrentActivity() : Activity? {
        return ExhibitionUIApplication.instance.getCurrentActivity()
    }

    /**
     * Starts a settings activity
     */
    protected fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Triggers on interaction in the ExhibitionUIApplication
     */
    protected fun onInteraction() {
        ExhibitionUIApplication.instance.onInteraction()
    }

    /**
     * Sets forced portrait mode
     */
    fun setForcedPortraitMode() {
        runOnUiThread {
            findViewById<RotateLayout>(R.id.main_screen_rotate)?.angle = 90
            findViewById<RotateLayout>(R.id.root)?.angle = 90
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        onInteraction()
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Starts Main Activity
     */
    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        VisitorSessionContainer.setVisitorSession(null)
        this.startActivity(intent)
        finish()
    }

    /**
     * Displays logout warning with a running timer
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
    fun cancelLogoutWarning(){
        countDownTimer?.cancel()
        logoutWarningToast?.cancel()
    }

    /**
     * Logs in with debug account
     */
    private fun debugLogin() {
        ExhibitionUIApplication.instance.visitorLogin(BuildConfig.KEYCLOAK_DEMO_TAG)
    }

    /**
     * Handler for settings button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun settingsButtonClick() {
        clickCounterHandler.removeCallbacksAndMessages("settings")
        settingsClickCounter += 1
        if (settingsClickCounter > 4) {
            startSettingsActivity()
        } else {
            clickCounterHandler.postDelayed({
                settingsClickCounter = 0
            }, "settings", 1000)
        }
    }

    /**
     * Handler for settings button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun indexButtonClick() {
        clickCounterHandler.removeCallbacksAndMessages("index")
        settingsClickCounter += 1
        if (settingsClickCounter > 4) {
            startMainActivity()
        } else {
            clickCounterHandler.postDelayed({
                settingsClickCounter = 0
            }, "index", 1000)
        }
    }

    /**
     * Handler for login button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun loginButtonClick() {
        clickCounterHandler.removeCallbacksAndMessages("login")
        settingsClickCounter += 1
        if (settingsClickCounter > 4) {
            debugLogin()
        } else {
            clickCounterHandler.postDelayed( {
                settingsClickCounter = 0
            }, "login", 1000)
        }
    }
}
