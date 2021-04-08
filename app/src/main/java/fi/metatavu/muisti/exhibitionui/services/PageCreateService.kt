package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Service for creating layouts, pages and constructing pages
 */
class PageCreateService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            try {
                UpdateLayouts.updateAllLayouts().join()
                UpdatePages.updateAllPages().join()
                ConstructPagesService.constructAllPages().join()
            } catch (e: Exception) {
                Log.d(javaClass.name, "Pages update failed", e)
            }
        }
    }
}