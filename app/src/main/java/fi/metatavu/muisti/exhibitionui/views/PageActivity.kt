package fi.metatavu.muisti.exhibitionui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import fi.metatavu.muisti.exhibitionui.R
import android.view.Gravity
import android.widget.LinearLayout
import android.util.Log
import android.widget.TextView
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.pages.PageLayoutContainer
import fi.metatavu.muisti.exhibitionui.pages.components.TextViewComponentFactory
import java.io.*
import java.time.Instant.now
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class PageActivity : AppCompatActivity() {

    init {
        val executor = Executors.newSingleThreadScheduledExecutor()


        executor.scheduleAtFixedRate({
            enqueueTask()
        }, 1, 1, TimeUnit.SECONDS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        val layout = LinearLayout(this)
        layout.gravity = Gravity.CENTER
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val button = Button(this)
        button.setText("PERUNA?")

        layout.addView(button, params)
        val mappi = mutableMapOf<String, LinearLayout>()
        mappi.put("bataatti", layout)

        val layout = LinearLayout(this)
        layout.gravity = Gravity.CENTER
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val button = Button(this)
        button.setText("PERUNA?")

        layout.addView(button, params)

        val mappi = mutableMapOf<String, LinearLayout>()
        mappi.put("bataatti", layout)

        val textViewComponent = TextViewComponentFactory().buildComponent(layout.context)
        layout.addView(textViewComponent) */
        val view = PageLayoutContainer.get(intent.getStringExtra("pageId"))
        val textView = view.findViewWithTag<TextView>("porkkana")
        if(textView != null){
            textView.text = now().toString()
        }
        setContentView(view)
    }

    fun enqueueTask(){
        val textView = window.decorView.findViewWithTag<TextView>("porkkana")
        if(textView != null){
            //textView.width
            textView.text = now().toString()
        }
    }

    fun <T> serialize(task: T): ByteArray? {
        try {
            ByteArrayOutputStream().use { resultStream ->
                serializeToStream(task, resultStream)
                resultStream.flush()
                return resultStream.toByteArray()
            }
        } catch (e: IOException) {
            Log.e(javaClass.name, "Failed to write serialized task data", e)
        }

        return null
    }

    fun <T> unserialize(rawData: ByteArray): T? {
        try {
            ByteArrayInputStream(rawData).use { byteStream ->
                return unserializeFromStream(
                    byteStream
                )
            }
        } catch (e: IOException) {
            Log.e(javaClass.name,  "Failed to write unserialized task data", e)
        }

        return null
    }

    private fun <T> serializeToStream(task: T, stream: OutputStream) {
        try {
            ObjectOutputStream(stream).use { objectStream ->
                objectStream.writeObject(task)
                objectStream.flush()
            }
        } catch (e: IOException) {
            Log.e(javaClass.name, "Failed to serialize task", e)
        }

    }

    private fun <T> unserializeFromStream(stream: InputStream): T? {
        try {
            ObjectInputStream(stream).use { objectStream ->
                val `object` = objectStream.readObject() ?: return null

                return `object` as T
            }
        } catch (e: IOException) {
            Log.e(javaClass.name, "Failed to unserialize task", e)
        } catch (e: ClassNotFoundException) {
            Log.e(javaClass.name, "Failed to unserialize task", e)
        }

        return null
    }

}
