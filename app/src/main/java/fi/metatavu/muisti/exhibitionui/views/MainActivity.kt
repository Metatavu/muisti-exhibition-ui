package fi.metatavu.muisti.exhibitionui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Main activity class
 */
class MainActivity : MuistiActivity() {

    private var mViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        visitorLogin()
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
            val tagId = getDeviceId()
            mViewModel?.visitorLogin(exhibitionId, tagId)
            val frontPage = mViewModel?.getFrontPage(exhibitionId, deviceId)
            if(frontPage != null){
                goToPage(frontPage)
            } else {
                startPreviewActivity()
            }
        } else {
            startSettingsActivity()
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
     * Starts a preview activity
     */
    private fun startPreviewActivity() {
        val intent = Intent(this, PreviewActivity::class.java)
        this.startActivity(intent)
    }
}