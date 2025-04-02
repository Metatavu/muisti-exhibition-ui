package fi.metatavu.muisti.exhibitionui.views

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import fi.metatavu.muisti.api.client.models.DeviceType
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.settings.DeviceSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Setup activity
 */
class SetupActivity : MuistiActivity() {

    private lateinit var mViewModel: SetupViewModel
    private lateinit var deviceNameInput: EditText
    private lateinit var deviceDescriptionInput: EditText
    private lateinit var deviceNameLabel: TextView
    private lateinit var deviceDescriptionLabel: TextView
    private lateinit var saveSetupButton: Button
    private lateinit var setupStatusText: TextView
    private var pollingJob: Job? = null
    private val currentVersionCode: Int

        get() {
            @Suppress("DEPRECATION")
            return packageManager
                .getPackageInfo(packageName, 0)!!
                .versionCode
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_activity)

        mViewModel = ViewModelProvider(this).get(SetupViewModel::class.java)
        deviceNameInput = findViewById(R.id.deviceNameInput)
        deviceDescriptionInput = findViewById(R.id.deviceDescriptionInput)
        setupStatusText = findViewById(R.id.setupStatusText)
        saveSetupButton = findViewById(R.id.saveSetup)
        deviceNameLabel = findViewById(R.id.deviceNameLabel)
        deviceDescriptionLabel = findViewById(R.id.deviceDescriptionLabel)

        val saveSetupButton = findViewById<Button>(R.id.saveSetup)

        deviceNameInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    deviceDescriptionInput.requestFocus()

                    return@setOnKeyListener true
                }
            }
            false
        }

        deviceDescriptionInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    saveSetupButton.requestFocus()

                    return@setOnKeyListener true
                }
            }
            false
        }

        saveSetupButton.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    deviceDescriptionInput.requestFocus()

                    return@setOnKeyListener true
                }
            }
            false
        }

        saveSetupButton.setOnClickListener {
            createDeviceRequest()
        }
        startPollingDeviceApproval()
    }

    /**
     * Creates device request with required information
     */
    private fun createDeviceRequest() {
        val deviceName = deviceNameInput.text.toString()
        val deviceDescription = deviceDescriptionInput.text.toString()
        if (deviceName.isEmpty() || deviceDescription.isEmpty()) {
            Toast.makeText(this@SetupActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()

            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val serialNumber = getSerialNumber()
            val device = mViewModel.createDevice(
                name = deviceName,
                description = deviceDescription,
                deviceType = DeviceType.cUSTOM,
                serialNumber = serialNumber,
                version = currentVersionCode.toString()
            )
            DeviceSettings.setDeviceId(device.id!!)
            withContext(Dispatchers.Main) {
                hideSetupUI()

                setupStatusText.text = getString(R.string.waitSetupApproval)
                setupStatusText.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Hides setup UI
     */
    private fun hideSetupUI() {
        deviceNameInput.visibility = View.GONE
        deviceDescriptionInput.visibility = View.GONE
        saveSetupButton.visibility = View.GONE
        deviceNameLabel.visibility = View.GONE
        deviceDescriptionLabel.visibility = View.GONE
    }

    /**
     * Polling while waiting device approval
     */
    private fun startPollingDeviceApproval() {
        pollingJob?.cancel()
        pollingJob = CoroutineScope(Dispatchers.IO).launch {

            while (isActive) {
                delay(5000)
                Log.d("Setupactivity","Polling")
                val deviceId = DeviceSettings.getDeviceId()

                if (deviceId == null) {
                    Log.d("Setupactivity","Waiting for setup...")
                    continue
                }

                val deviceKey = mViewModel.requestDeviceKey(deviceId = deviceId)
                if(deviceKey != null){
                    mViewModel.setDeviceKey(deviceKey = deviceKey.key)

                    withContext(Dispatchers.Main) {
                        setupStatusText.text = getString(R.string.deviceApproved)
                    }
                    pollingJob?.cancel()
                }
            }
        }
    }

    /**
     * Gets Android device serial number
     */
    private fun getSerialNumber(): String {
        return Settings.Secure.getString(
            getApplicationContext().getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
    }
}
