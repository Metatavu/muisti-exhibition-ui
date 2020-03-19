package fi.metatavu.muisti.exhibitionui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Main activity class
 */
class MainActivity : AppCompatActivity() {

    private var mViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        checkSettings()
        visitorLogin()
    }

    /**
     * Checks that all required settings are set and if not redirects user to settings view
     */
    private fun checkSettings() = lifecycleScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()

        if (exhibitionId == null) {
            startSettingsActivity()
        }
    }

    /**
     * Logs the visitor in to the system and redirect user forward activity then done
     */
    private fun visitorLogin() = GlobalScope.launch {
        val exhibitionId = DeviceSettings.getExhibitionId()
        if (exhibitionId != null) {
            val tagId = getDeviceId()
            mViewModel?.visitorLogin(exhibitionId, tagId)
            startPreviewActivity()
        }
    }

    /**
     * Returns a device id
     *
     * @return device id
     */
    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        try {
            return Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            return "unknown"
        }
    }


    /**
     * Starts a settings activity
     */
    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Starts a preview activity
     */
    private fun startPreviewActivity() {
        val intent = Intent(this, PreviewActivity::class.java)
        this.startActivity(intent)
    }
}