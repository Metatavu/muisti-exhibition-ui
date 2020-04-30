package fi.metatavu.muisti.exhibitionui.views

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import java.util.*

/**
 * Muisti activity abstract class
 */
abstract class MuistiActivity : AppCompatActivity(){
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
        overridePendingTransition(0, R.anim.test_transition)
    }

    /**
     * Starts a settings activity
     */
    protected fun startSettingsActivity(options: ActivityOptions? = null) {
        val intent = Intent(this, SettingsActivity::class.java)
        if(options != null){
            this.startActivity(intent, options.toBundle())
        } else {
            this.startActivity(intent)
        }
    }

    /**
     * Starts Main Activity
     */
    protected fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }
}
