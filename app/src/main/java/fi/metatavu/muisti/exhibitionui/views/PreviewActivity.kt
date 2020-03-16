package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageView

import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.content_preview.*
import java.util.*

/**
 * Preview activity class
 */
class PreviewActivity : AppCompatActivity() {

    private var mViewModel: PreviewViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(this).get(PreviewViewModel::class.java)

        setContentView(R.layout.activity_preview)
        setSupportActionBar(toolbar)

        previewPageButton.setOnClickListener(onPreviewPageButtonClick)

        mViewModel?.livePageViewList()?.observe(this, Observer {
            pageViews -> run {
                pageSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pageViews)
            }
        })
    }

    /**
     * Opens a page view
     *
     * @param pageId page id
     */
    private fun goToPage(pageId: UUID) {
        val intent = Intent(this, PageActivity::class.java).apply{
            putExtra("pageId", pageId.toString())
        }

        startActivity(intent)
    }

    /**
     * Event handler for preview page button click
     */
    private val onPreviewPageButtonClick = View.OnClickListener {
        val pageView: PageView? = pageSpinner.selectedItem as PageView?
        val pageId = pageView?.id
        if (pageId != null) {
            goToPage(pageId)
        }
    }

}
