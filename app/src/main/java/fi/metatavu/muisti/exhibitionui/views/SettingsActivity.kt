package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch
import fi.metatavu.muisti.exhibitionui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Settings activity
 */
class SettingsActivity : MuistiActivity() {

    private var mViewModel: SettingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    fun exitSettings(view: View?) {
        val intent = Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {
        exitSettings(null)
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

                    val exhibitionPreference: ListPreference = findPreference<ListPreference>("exhibition")!!
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

            val rfidDevicePreference: EditTextPreference = findPreference("rfid_device")!!
            rfidDevicePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                //onExhibitionRfidDevicePreferenceChange(preference as EditTextPreference, newValue as String)
                true
            }

            val rfidAntennaPreference: EditTextPreference = findPreference("rfid_antenna")!!
            rfidAntennaPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                onExhibitionRfidAntennaPreferenceChange(preference as EditTextPreference, newValue as String)
                true
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
         * Updates a preference list summary
         *
         * @param listPreference list preference
         * @param newValue list value
         */
        private fun updateTextPreferenceSummary(textPreference: EditTextPreference, newValue: String?) {
            if(newValue.isNullOrEmpty()){
                textPreference.summary = getString(R.string.exhibition_perference_not_set)
            } else {
                textPreference.summary = newValue
            }
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

            val exhibitionDevicesPreference: ListPreference = findPreference<ListPreference>("exhibition_device")!!
            exhibitionDevicesPreference.entries = exhibitionDevices?.map { it.name }?.toTypedArray()
            exhibitionDevicesPreference.entryValues = exhibitionDevices?.map { it.id.toString() }?.toTypedArray()
            exhibitionDevicesPreference.value = exhibitionId?.toString()
            exhibitionDevicesPreference.summary = exhibitionDevice?.name
            updateListPreferenceSummary(exhibitionDevicesPreference, exhibitionDeviceId?.toString())
        }

        /**
         * Loads exhibition devices preference data
         *
         * @param exhibitionId exhibition id
         */
        private suspend fun loadRfidSettings() {
            /*val rfidAntennaPreference: EditTextPreference = findPreference("rfid_antennas")!!
            rfidAntennaPreference.text = getRfidAntenna()
            rfidAntennaPreference.summary = getRfidAntenna() */
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
         * Returns exhibition device id from device setting
         *
         * @return exhibition device id
         */
        private suspend fun getRfidAntenna(): String? = withContext(Dispatchers.Default) {
            mViewModel?.getRfidAntenna()
        }

        /**
         * Event handler for exhibition list change
         *
         * @param exhibitionPreference
         * @param newValue
         */
        private fun onExhibitionPreferenceChange(exhibitionPreference: ListPreference, newValue: String) {
            val exhibitionDevicePreference: ListPreference = findPreference<ListPreference>("exhibition_device")!!
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
        }

        /**
         * Event handler for exhibition device list change
         *
         * @param exhibitionDevicePreference
         * @param newValue
         */
        private fun onExhibitionRfidAntennaPreferenceChange(rfidAntennaPreference: EditTextPreference, newValue: String) {
            updateTextPreferenceSummary(rfidAntennaPreference, newValue)
            //mViewModel?.setExhibitionAntennas(newValue)
        }
    }
}