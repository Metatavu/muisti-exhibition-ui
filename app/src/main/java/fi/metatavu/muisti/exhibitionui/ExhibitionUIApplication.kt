package fi.metatavu.muisti.exhibitionui

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.persistence.model.UpdateUserValueTask
import fi.metatavu.muisti.exhibitionui.services.UpdateUserValueService
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor

class ExhibitionUIApplication : Application() {

    init {
        instance = this
        val executor = Executors.newSingleThreadScheduledExecutor()


        executor.scheduleAtFixedRate({
            enqueueTask()
        }, 1, 1, TimeUnit.SECONDS)
    }

    private fun enqueueTask() {
        var serviceIntent = Intent().apply { }

        JobIntentService.enqueueWork(this, UpdateUserValueService::class.java, 1000, serviceIntent)

    }

    companion object {
        lateinit var instance: ExhibitionUIApplication
    }

}