package fi.metatavu.muisti.exhibitionui.services

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.pages.PageViewFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Service for constructing page view
 */
class ConstructPagesService : JobIntentService() {

    private val pageRepository: PageRepository = PageRepository(ExhibitionUIDatabase.getDatabase().pageDao())
    private val layoutRepository: LayoutRepository = LayoutRepository(ExhibitionUIDatabase.getDatabase().layoutDao())

    override fun onHandleWork(intent: Intent) {
        GlobalScope.launch {
            val pages = pageRepository.listAll()
            if (pages!= null) {
                Log.d(ConstructPagesService::javaClass.name, "Constructing ${pages.size} pages...")

                for (page in pages) {
                    constructPage(page)
                }
            }
        }
    }

    /**
     * Constructs a page view
     *
     * @param page page
     */
    private suspend fun constructPage(page: Page) {
        val pageId = page.pageId
        val layoutId = page.layoutId
        val layout = layoutRepository.getLayout(layoutId)

        if (layout != null) {
            Log.d(ConstructPagesService::javaClass.name, "Constructing page ${pageId}...")
            val context = ExhibitionUIApplication.instance.applicationContext
            val view = PageViewFactory.buildPageView(context, layout.data)
            if (view != null) {
                PageViewContainer.set(pageId, view)
                Log.d(ConstructPagesService::javaClass.name, "Constructed page ${page.name} - ${pageId}.")
            } else {
                Log.d(ConstructPagesService::javaClass.name,"Could not construct page ${pageId}.")
            }
        } else {
            Log.d(ConstructPagesService::javaClass.name,"Could not construct page ${pageId} because layout ${layoutId} does not exist.")
        }
    }
}