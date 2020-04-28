package fi.metatavu.muisti.exhibitionui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.api.MuistiApiFactory
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
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
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        visitorLogin()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
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
            if (frontPage != null) {
                waitForPage(frontPage)
            } else {
                startPreviewActivity()
            }
        } else {
            startSettingsActivity()
        }
    }

    /**
     * Checks if page is constructed in the PageViewContainer and either navigates to it or keeps waiting
     *
     * @param pageId page id to navigate to once it is ready
     */
    private fun waitForPage(pageId: UUID){
        handler.postDelayed({
            if(PageViewContainer.contains(pageId)){
                goToPage(pageId)
            } else {
                waitForPage(pageId)
            }
        }, "visitorLogin", 500)
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
