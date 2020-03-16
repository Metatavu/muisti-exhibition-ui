package fi.metatavu.muisti.exhibitionui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import fi.metatavu.muisti.exhibitionui.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity class
 */
class MainActivity : AppCompatActivity() {

    private var mViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        previewButton.setOnClickListener(onPreviewButtonwClick)
        settingsButton.setOnClickListener(onSettingsButtonClick)
    }

    /**
     * Event handler for preview button click event
     */
    private val onPreviewButtonwClick = View.OnClickListener {
        val intent = Intent(this, PreviewActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Event handler for settings button click event
     */
    private val onSettingsButtonClick = View.OnClickListener {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }
}
