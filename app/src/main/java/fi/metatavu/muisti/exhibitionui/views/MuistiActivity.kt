package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
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
    protected fun goToPage(pageId: UUID) {
        val intent = Intent(this, PageActivity::class.java).apply{
            putExtra("pageId", pageId.toString())
        }

        startActivity(intent)
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
}
