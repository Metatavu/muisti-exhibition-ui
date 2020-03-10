package fi.metatavu.muisti.exhibitionui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import java.util.*

/**
 * Activity for displaying pages from API
 */
class PageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = PageViewContainer.get(UUID.fromString(intent.getStringExtra("pageId")))
        setContentView(view)
    }

}
