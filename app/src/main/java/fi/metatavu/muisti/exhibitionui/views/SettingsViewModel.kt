package fi.metatavu.muisti.exhibitionui.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask
import fi.metatavu.muisti.exhibitionui.persistence.repository.DeviceSettingRepository
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import kotlinx.coroutines.launch

/**
 * View model for test activity
 *
 * @constructor
 * model constructor
 *
 * @param application application instance
 */
class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private val deviceSettingRepository: DeviceSettingRepository

    init {
        val deviceSettingDao = ExhibitionUIDatabase.getDatabase().deviceSettingDao()
        deviceSettingRepository = DeviceSettingRepository(deviceSettingDao)
    }

    fun listExhibitions(): List<String> {
        return listOf<String>("dummy 1", "dummy 2")
    }
}