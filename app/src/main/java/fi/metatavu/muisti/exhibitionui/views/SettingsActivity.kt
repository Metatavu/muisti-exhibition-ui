package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fi.metatavu.muisti.api.client.models.Exhibition
import fi.metatavu.muisti.api.client.models.ExhibitionDevice
import fi.metatavu.muisti.api.client.models.RfidAntenna
import kotlinx.coroutines.launch
import fi.metatavu.muisti.exhibitionui.services.UpdateRfidAntenna
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import android.widget.ArrayAdapter
import fi.metatavu.muisti.exhibitionui.R


/**
 * Settings activity
 */
class SettingsActivity : MuistiActivity() {

    private var mViewModel: SettingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        setContentView(R.layout.settings_activity)
        val display = DisplayMetrics()

        windowManager.defaultDisplay.getRealMetrics(display)

        val list = mutableListOf(
            "density: " + display.density,
            "densityDpi: " + display.densityDpi,
            "heightPixels: " + display.heightPixels,
            "widthPixels: " + display.widthPixels,
            "scaledDensity: " + display.scaledDensity,
            "xdpi: " + display.xdpi,
            "ydpi: " + display.ydpi
        )

        val adapter = ArrayAdapter<String>(applicationContext, R.layout.display_metric, list)
        display_metrics.adapter = adapter
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    fun exitSettings(view: View?) {
        val intent = Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {
        exitSettings(null)
    }
}

/**
 * Settings preference fragment
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private var mViewModel: SettingsViewModel? = null

    init {
        lifecycleScope.launch {
            whenStarted {
                val exhibitions = listExhibitions()
                val exhibitionId = getExhibitionId()
                val exhibition = exhibitions.find { it.id!!.equals(exhibitionId) }

                val exhibitionPreference: ListPreference = findPreference("exhibition")!!
                exhibitionPreference.entries = exhibitions.map { it.name }.toTypedArray()
                exhibitionPreference.entryValues = exhibitions.map { it.id.toString() }.toTypedArray()
                exhibitionPreference.value = exhibitionId.toString()
                exhibitionPreference.summary = exhibition?.name

                loadExhibitionDevicesPreference(exhibitionId)
                loadRfidSettings()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        val exhibitionPreference: ListPreference = findPreference("exhibition")!!
        exhibitionPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            onExhibitionPreferenceChange(preference as ListPreference, newValue as String)
            true
        }

        val exhibitionDevicePreference: ListPreference = findPreference("exhibition_device")!!
        exhibitionDevicePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            onExhibitionDevicePreferenceChange(preference as ListPreference, newValue as String)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        UpdateRfidAntenna.addAntennaUpdateListener {
            reloadRfidSettings()
        }
    }

    override fun onPause() {
        super.onPause()
        UpdateRfidAntenna.removeAntennaUpdateListener {
            reloadRfidSettings()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    /**
     * Updates a preference list summary
     *
     * @param listPreference list preference
     * @param newValue list value
     */
    private fun updateListPreferenceSummary(listPreference: ListPreference, newValue: String?) {
        val index = listPreference.findIndexOfValue(newValue)
        if (index > -1)
            listPreference.summary = listPreference.entries[index]
        else
            listPreference.summary = getString(R.string.exhibition_perference_not_set)
    }

    /**
     * Reloads exhibition devices preference data
     *
     * @param exhibitionId exhibition id
     */
    private fun reloadExhibitionDevicesPreference(exhibitionId: UUID?) = lifecycleScope.launch {
        loadExhibitionDevicesPreference(exhibitionId)
    }

    /**
     * Loads exhibition devices preference data
     *
     * @param exhibitionId exhibition id
     */
    private suspend fun loadExhibitionDevicesPreference(exhibitionId: UUID?) {
        var exhibitionDevices: Array<ExhibitionDevice>? = null
        val exhibitionDeviceId = getExhibitionDeviceId()

        if (exhibitionId != null) {
            exhibitionDevices = listExhibitionDevices(exhibitionId)
        }

        val exhibitionDevice = exhibitionDevices?.find { it.id!!.equals(exhibitionDeviceId) }

        val exhibitionDevicesPreference: ListPreference = findPreference("exhibition_device")!!
        exhibitionDevicesPreference.entries = exhibitionDevices?.map { it.name }?.toTypedArray() ?: emptyArray()
        exhibitionDevicesPreference.entryValues = exhibitionDevices?.map { it.id.toString() }?.toTypedArray()  ?: emptyArray()
        exhibitionDevicesPreference.value = exhibitionId?.toString()
        exhibitionDevicesPreference.summary = exhibitionDevice?.name
        updateListPreferenceSummary(exhibitionDevicesPreference, exhibitionDeviceId?.toString())
    }

    /**
     * Reloads Rfid settings
     */
    private fun reloadRfidSettings() = lifecycleScope.launch {
        loadRfidSettings()
    }

    /**
     * Loads Rfid settings
     */
    private suspend fun loadRfidSettings() {
        val rfidAntennaPreference: EditTextPreference = findPreference("rfid_antennas") ?: return
        val antennas = toAntennaString(getRfidAntennas()).joinToString("\n")
        rfidAntennaPreference.text = antennas
        rfidAntennaPreference.summary = antennas
    }

    /**
     * Lists all available exhibitions from API
     *
     * @return exhibitions from API
     */
    private suspend fun listExhibitions(): Array<Exhibition> = withContext(Dispatchers.Default) {
        mViewModel!!.listExhibitions()
    }

    /**
     * Lists all available exhibition devices from API
     *
     * @return exhibition devices from API
     */
    private suspend fun listExhibitionDevices(exhibitionId: UUID): Array<ExhibitionDevice> = withContext(Dispatchers.Default) {
        mViewModel!!.listExhibitionDevices(exhibitionId, null)
    }

    /**
     * Returns exhibition id from device setting
     *
     * @return exhibition id
     */
    private suspend fun getExhibitionId(): UUID? = withContext(Dispatchers.Default) {
        mViewModel?.getExhibitionId()
    }

    /**
     * Returns exhibition device id from device setting
     *
     * @return exhibition device id
     */
    private suspend fun getExhibitionDeviceId(): UUID? = withContext(Dispatchers.Default) {
        mViewModel?.getExhibitionDeviceId()
    }

    /**
     * Returns a list of rfid antennas from device settings
     *
     * @return exhibition device id
     */
    private suspend fun getRfidAntennas(): List<RfidAntenna>? = withContext(Dispatchers.Default) {
        mViewModel?.getRfidAntennas()
    }

    /**
     * Event handler for exhibition list change
     *
     * @param exhibitionPreference
     * @param newValue
     */
    private fun onExhibitionPreferenceChange(exhibitionPreference: ListPreference, newValue: String) {
        val exhibitionDevicePreference: ListPreference = findPreference("exhibition_device")!!
        exhibitionDevicePreference.summary = getString(R.string.exhibition_perference_loading)
        mViewModel?.setExhibitionDeviceId(null)
        mViewModel?.setExhibitionId(newValue)
        updateListPreferenceSummary(exhibitionPreference, newValue)
        updateListPreferenceSummary(exhibitionDevicePreference, null)
        reloadExhibitionDevicesPreference(UUID.fromString(newValue))
    }

    /**
     * Event handler for exhibition device list change
     *
     * @param exhibitionDevicePreference
     * @param newValue
     */
    private fun onExhibitionDevicePreferenceChange(exhibitionDevicePreference: ListPreference, newValue: String) {
        updateListPreferenceSummary(exhibitionDevicePreference, newValue)
        mViewModel?.setExhibitionDeviceId(newValue)
        lifecycleScope.launch {
            UpdateRfidAntenna.updateDeviceAntennas()
        }
    }

    /**
     * Converts Rfid Antenna to mqtt antenna path string
     *
     * @param rfidAntennas List of Rfid Antennas to convert
     * @return List of mqtt antenna path strings
     */
    private fun toAntennaString(rfidAntennas: List<RfidAntenna>?): List<String> {
        return rfidAntennas?.map { "${it.readerId}/${it.antennaNumber}/" } ?: emptyList()
    }
}