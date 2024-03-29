package fi.metatavu.muisti.exhibitionui.services

import android.util.Log
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import fi.metatavu.muisti.exhibitionui.pages.PageViewFactory
import fi.metatavu.muisti.exhibitionui.persistence.ExhibitionUIDatabase
import fi.metatavu.muisti.exhibitionui.persistence.model.Page
import fi.metatavu.muisti.exhibitionui.persistence.repository.LayoutRepository
import fi.metatavu.muisti.exhibitionui.persistence.repository.PageRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Service for constructing page view
 */
object ConstructPagesService {

    private val pageRepository: PageRepository = PageRepository(ExhibitionUIDatabase.getDatabase().pageDao())
    private val layoutRepository: LayoutRepository = LayoutRepository(ExhibitionUIDatabase.getDatabase().layoutDao())

    fun constructAllPages() = GlobalScope.launch {
        val pages = pageRepository.listAll()
        if (pages != null) {
            for (page in pages) {
                constructPage(page)
            }
        }
    }

    suspend fun constructPage(page: Page) {
        try {
            val pageId = page.pageId
            val layoutId = page.layoutId
            val layout = layoutRepository.getLayout(layoutId)

            if (layout != null) {
                val context = ExhibitionUIApplication.instance.applicationContext
                context.setTheme(R.style.AppTheme)
                val pageView = PageViewFactory.buildPageView(context, page, layout)
                if (pageView != null) {
                    PageViewContainer.setPageView(pageId, pageView)
                } else {
                    Log.d(ConstructPagesService::javaClass.name, "Could not construct page ${pageId}.")
                }
            } else {
                Log.d(ConstructPagesService::javaClass.name, "Could not construct page ${pageId} because layout ${layoutId} does not exist.")
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "Failed to construct page", e)
        }
    }
}