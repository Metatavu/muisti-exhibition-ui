package fi.metatavu.muisti.exhibitionui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import kotlinx.android.synthetic.main.activity_page.*
import kotlinx.android.synthetic.main.activity_update.infoText
import kotlinx.android.synthetic.main.activity_update.skipButton
import kotlinx.android.synthetic.main.activity_update.updateButton
import kotlinx.android.synthetic.main.activity_update.loader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File

/**
 * Update application activity class
 */
@SuppressLint("SetTextI18n")
class UpdateActivity : Activity()
{
    private val updateUrl: String
        get() = BuildConfig.UPDATE_URL

    private val apkFileName = "fi.metatavu.muisti.exhibitionui.apk"

    private var updateInfo: UpdateInfo? = null

    private val downloadsDir = ExhibitionUIApplication.instance.applicationContext.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)!!
    private val currentVersionCode: Int
        get() {
            @Suppress("DEPRECATION")
            return packageManager
                .getPackageInfo(packageName, 0)!!
                .versionCode
        }

    private val currentVersionName: String
        get() {
            return packageManager
                .getPackageInfo(packageName, 0)!!
                .versionName
        }

    /**
     * Activity onCreate event handler
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        skipButton.setOnClickListener {
            startApplication()
        }

        updateButton.setOnClickListener {
            installUpdate()
        }
    }

    /**
     * Activity onResume event handler
     */
    override fun onResume() {
        super.onResume()
        checkUpdate()
    }

    /**
     * Continues with normal application startup by starting loading activity
     */
    private fun startApplication() {
        val intent = Intent(this, LoadingActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Checks for updates from the server
     */
    private fun checkUpdate() = GlobalScope.launch {
        showLoader()

        try {
            infoText.text = "Checking for updates..."
            Log.d(javaClass.name, "Checking for software update...")

            val client = OkHttpClient.Builder().build()

            val outputJsonResult = client
                .newCall(Request.Builder().url("$updateUrl/output-metadata.json").build())
                .execute()

            if (outputJsonResult.isSuccessful) {
                try {
                    val outputMetadata = parseOutputMetadata(outputJsonResult)
                    val versionElement = outputMetadata?.elements?.first()
                    if (versionElement == null) {
                        Log.e(javaClass.name, "Could not parse version from output-metadata.json")
                        startApplication()
                    } else {
                        val versionCode = versionElement.versionCode
                        val versionName = versionElement.versionName
                        val apkFile = versionElement.outputFile

                        updateInfo = UpdateInfo(
                            versionCode = versionCode,
                            versionName = versionName,
                            apkFile = apkFile
                        )

                        if (versionCode > currentVersionCode) {
                            infoText.text = "Update from $currentVersionName to $versionName available!"
                            showButtons()
                            hideLoader()
                        } else {
                            infoText.text = "No updates available"
                            Log.d(javaClass.name, "No new software update available")
                            startApplication()
                        }
                    }
                } catch (ex: Exception) {
                    Log.e(javaClass.name, "Error when parsing output-metadata.json", ex)
                    showError("Error when parsing output-metadata.json")
                }
            } else {
                Log.e(javaClass.name, "Couldn't download version number: ${outputJsonResult.message}")
                showError("Couldn't download version number: ${outputJsonResult.message}")
            }
        } catch (ex: Exception) {
            Log.e(javaClass.name, "Error when updating software: $ex")
            showError("Error when updating software: $ex")
        } finally {
            hideLoader()
        }
    }

    /**
     * Installs update
     */
    private fun installUpdate() = GlobalScope.launch {
        val installInfo = updateInfo ?: return@launch

        showLoader()
        hideButtons()

        Log.d(javaClass.name, "Downloading update ${installInfo.versionCode}...")
        infoText.text = "Downloading update ${installInfo.versionName}..."

        val fileResult = OkHttpClient.Builder().build()
            .newCall(Request.Builder().url("$updateUrl/${installInfo.apkFile}").build())
            .execute()

        if (fileResult.isSuccessful) {
            Log.d(javaClass.name, "Installing update ${installInfo.versionCode}")
            infoText.text = "Installing update ${installInfo.versionName}..."

            Log.d(javaClass.name, "Successfully downloaded software update!")
            fileResult.body!!.byteStream().use {

                val apkFile = downloadsDir.resolve(apkFileName)
                if (apkFile.exists()) {
                    apkFile.delete()
                }

                apkFile.createNewFile()

                apkFile.outputStream().use { output ->
                    it.copyTo(output)
                }

                installApk(Uri.fromFile(apkFile))

                hideLoader()
            }

        } else {
            Log.e(
                javaClass.name,
                "Couldn't download software update: ${fileResult.message}"
            )

            showError("Failed to check updates: ${fileResult.message}")

            startApplication()
        }
    }

    /**
     * Installs apk
     */
    private fun installApk(apkUri: Uri) {
        val install = Intent(Intent.ACTION_VIEW)
        install.setDataAndType(apkUri, "application/vnd.android.package-archive")
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val contentUri = FileProvider.getUriForFile(this, "fi.metatavu.muisti.exhibitionui.fileProvider", File(apkUri.path!!))
        install.setDataAndType(contentUri, "application/vnd.android.package-archive")
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            this.startActivity(install)
        } catch (e: Exception) {
            Log.e(javaClass.name, "Error when installing apk: $e")
            startApplication()
        }
    }

    /**
     * Shows error message
     *
     * @param message message to show
     */
    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Shows loader
     */
    private fun showLoader() {
        runOnUiThread {
            loader.visibility = View.VISIBLE
        }
    }

    /**
     * Hides loader
     */
    private fun hideLoader() {
        runOnUiThread {
            loader.visibility = View.INVISIBLE
        }
    }

    /**
     * Shows skip and update buttons
     */
    private fun showButtons() {
        skipButton.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
    }

    /**
     * Hides skip and update buttons
     */
    private fun hideButtons() {
        skipButton.visibility = View.INVISIBLE
        updateButton.visibility = View.INVISIBLE
    }


    /**
     * Parses output metadata from server response
     *
     * @param response response
     * @return parsed output metadata
     */
    private fun parseOutputMetadata(response: Response): OutputMetadata? = response.body!!.source().use {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(OutputMetadata::class.java)
        return adapter.fromJson(it)
    }

    /**
     * Update info data class
     */
    data class UpdateInfo(
        val versionCode: Int,
        val versionName: String,
        val apkFile: String
    )

    /**
     * Moshi model for output-metadata.json
     */
    @JsonClass(generateAdapter = true)
    data class OutputMetadata(
        val version: Int,
        @Json(name = "artifactType") val artifactType: ArtifactType,
        @Json(name = "applicationId") val applicationId: String,
        @Json(name = "variantName") val variantName: String,
        @Json(name = "elements") val elements: List<Element>
    )

    /**
     * Moshi model for artifactType
     */
    @JsonClass(generateAdapter = true)
    data class ArtifactType(
        val type: String,
        val kind: String
    )

    /**
     * Moshi model for element
     */
    @JsonClass(generateAdapter = true)
    data class Element(
        val type: String,
        val filters: List<String>,
        @Json(name = "versionCode") val versionCode: Int,
        @Json(name = "versionName") val versionName: String,
        @Json(name = "outputFile") val outputFile: String
    )
}
