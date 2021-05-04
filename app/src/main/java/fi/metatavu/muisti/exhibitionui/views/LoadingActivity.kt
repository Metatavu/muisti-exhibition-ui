package fi.metatavu.muisti.exhibitionui.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.services.ConstructPagesService
import fi.metatavu.muisti.exhibitionui.services.UpdateLayouts
import fi.metatavu.muisti.exhibitionui.services.UpdatePages
import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Loading activity class
 */
class LoadingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        settings_button.setOnClickListener {
            startSettingsActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        loadContents()
    }

    /**
     * Preloads all devices contents and moves to main activity
     */
    private fun loadContents() = GlobalScope.launch {
        Log.d(LoadingActivity::class.java.name, "Loading layouts...")
        UpdateLayouts.updateAllLayouts().join()

        Log.d(LoadingActivity::class.java.name, "Loading pages...")
        UpdatePages.updateAllPages().join()

        Log.d(LoadingActivity::class.java.name, "Constructing pages...")
        ConstructPagesService.constructAllPages().join()

        Log.d(LoadingActivity::class.java.name, "Load ready, starting main activity...")

        startMainActivity()
    }

    /**
     * Starts main activity
     */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Starts settings activity
     */
    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }

}
