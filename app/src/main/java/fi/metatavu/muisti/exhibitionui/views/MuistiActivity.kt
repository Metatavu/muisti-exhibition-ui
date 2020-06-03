package fi.metatavu.muisti.exhibitionui.views

import android.app.ActivityOptions
import android.content.Intent
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import java.util.*

/**
 * Muisti activity abstract class
 */
abstract class MuistiActivity : AppCompatActivity() {

    private var settingsClickCOunter = 0
    private val clickCounterHandler = Handler()

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
     * Removes settings and index page listeners
     */
    protected fun removeSettingsAndIndexListeners() {
        clickCounterHandler.removeCallbacksAndMessages(null)
    }

    /**
     * Opens a page view
     *
     * @param pageId page id
     */
    fun goToPage(pageId: UUID) {
        val intent = Intent(this, PageActivity::class.java).apply{
            putExtra("pageId", pageId.toString())
        }
        startActivity(intent)
        finish()
        overridePendingTransition(0, R.anim.basic_fade)
    }

    /**
     * Sets current activity into application
     *
     * @param activity activity
     */
    protected fun setCurrentActivity(activity: PageActivity?) {
        val application: ExhibitionUIApplication = this.applicationContext as ExhibitionUIApplication
        application.setCurrentActivity(activity)
    }

    /**
     * Starts a settings activity
     */
    protected fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Starts Main Activity
     */
    protected fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Handler for settings button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun settingsButtonClick() {
        clickCounterHandler.removeCallbacksAndMessages("settings")
        settingsClickCOunter += 1
        if (settingsClickCOunter > 4) {
            startSettingsActivity()
        } else {
            clickCounterHandler.postDelayed({
                settingsClickCOunter = 0
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
        settingsClickCOunter += 1
        if (settingsClickCOunter > 4) {
            startMainActivity()
        } else {
            clickCounterHandler.postDelayed( {
                settingsClickCOunter = 0
            }, "index", 1000)
        }
    }
}
