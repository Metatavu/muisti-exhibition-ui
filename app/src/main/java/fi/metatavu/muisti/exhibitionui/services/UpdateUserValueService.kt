package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import android.widget.LinearLayout
import androidx.core.app.JobIntentService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fi.metatavu.muisti.exhibitionui.pages.PageLayoutContainer
import fi.metatavu.muisti.exhibitionui.pages.PageViewFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.repository.UpdateUserValueTaskRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import okhttp3.internal.waitMillis

class UpdateUserValueService : JobIntentService() {

    private val updateUserValueTaskRepository: UpdateUserValueTaskRepository

    private val pageViewFactory: PageViewFactory

    init {
        val updateUserValueTaskDao = ExhibitionUIDatabase.getDatabase().updateUserValueTaskDao()
        updateUserValueTaskRepository = UpdateUserValueTaskRepository(updateUserValueTaskDao)
        pageViewFactory = PageViewFactory()
    }

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            Log.d(javaClass.name, "Setting Layout to Map")
            pageViewFactory.setLayoutToMap()
            val updateUserValueTasks = updateUserValueTaskRepository.list(1)
            if (updateUserValueTasks.isNotEmpty()) {
                val task = updateUserValueTasks.get(0)
                Log.d(javaClass.name, "DELETE ID: " + task.id)
                updateUserValueTaskRepository.delete(task)
            }
            val tokenProvider = KeycloakAccessTokenProvider()
            //tokenProvider.getAccessToken()
        }
    }

}