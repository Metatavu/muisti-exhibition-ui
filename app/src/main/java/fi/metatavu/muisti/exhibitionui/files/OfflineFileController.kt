package fi.metatavu.muisti.exhibitionui.files

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import kotlin.math.max

/**
 * Controller class for offlined files
 */
class OfflineFileController {

    companion object {

        private val httpClient = OkHttpClient()
        private val downloadsDir = ExhibitionUIApplication.instance.applicationContext.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)

        /**
         * Returns offlined file for given URL
         *
         * @param url URL
         * @return offlined file or null if not available
         */
        @Synchronized fun getOfflineFile(url: URL): File? {
            val urlExternal = url.toExternalForm()
            try {
                return download(urlExternal)
            } catch (e: SocketTimeoutException) {
                Log.e(OfflineFileController::class.java.name, "Socket timeout while downloading $urlExternal")
            } catch (e: Exception) {
                Log.e(OfflineFileController::class.java.name, "Failed to download $urlExternal", e)
            }

            return null
        }

        /**
         * Returns offline image file scaled to max image width and height
         *
         * @param url URL
         * @param maxImageWidth image width
         * @param maxImageHeight image height
         * @return offline file or null if file is not image
         */
        @Synchronized fun getOfflineImageFile(url: URL, maxImageWidth: Int, maxImageHeight: Int): File? {
            val urlExternal = url.toExternalForm()
            val urlHash: String = md5(urlExternal)
            val extension: String = urlExternal.substring(urlExternal.lastIndexOf("."))
            val imageFileName = "img-$urlHash$extension"
            val offlineImageFile = File(downloadsDir, imageFileName)

            if (offlineImageFile.exists()) {
                Log.d(OfflineFileController::class.java.name, "Using offlined image file for $urlExternal")
                return offlineImageFile
            }

            val offlineFile = download(urlExternal = urlExternal)

            val bitmap = getFileBitmap(
                offlineFile = offlineFile,
                maxImageWidth = maxImageWidth,
                maxImageHeight = maxImageHeight
            )

            if (bitmap == null) {
                Log.d(OfflineFileController::class.java.name, "Failed to offline image file for $urlExternal. Possibly not an image file?")
                return null
            }

            FileOutputStream(offlineImageFile).use {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, it)
            }

            Log.d(OfflineFileController::class.java.name, "Offlined image file for $urlExternal")

            return offlineImageFile
        }

        /**
         * Downloads URL to offline file
         *
         * @param urlExternal external URL
         * @return offlined file if download has succeeded or null if not
         */
        private fun download(urlExternal: String): File? {
            val urlHash: String = md5(urlExternal)
            val extension: String = urlExternal.substring(urlExternal.lastIndexOf("."))
            val fileName = "$urlHash$extension"

            val file = File(downloadsDir, fileName)
            val request = buildRequest(file, urlExternal)
            val response = httpClient.newCall(request).execute()

            if (response.code == 304) {
                Log.d(OfflineFileController::class.java.name, "Using offlined $urlExternal")
                return file
            }

            if (response.code != 200) {
                val reason = response.body?.string() ?: "Unknown reason"
                Log.e(OfflineFileController::class.java.name, "Failed to download $urlExternal: [${response.code}]: $reason")
                return null
            }

            Log.d(OfflineFileController::class.java.name, "Downloading $urlExternal")

            val eTag = response.headers("eTag").firstOrNull()
            val responseBody = response.body ?: return null

            val filePart = File(downloadsDir, "$fileName.part")
            if (filePart.exists()) {
                filePart.delete()
            }

            filePart.parentFile?.mkdirs()
            filePart.createNewFile()

            val filePartStream = FileOutputStream(filePart)

            responseBody.byteStream().use { inputStream ->
                filePartStream.use {
                    inputStream.copyTo(filePartStream)
                }
            }

            if (file.exists()) {
                file.delete()
            }

            filePart.renameTo(file)

            writeFileMeta(
                file = file,
                eTag = eTag
            )

            Log.d(OfflineFileController::class.java.name, "Downloaded $urlExternal")

            return File(file.absolutePath)
        }

        /**
         * Builds request
         *
         * @param file file
         * @param urlExternal file external URL
         * @return request
         */
        private fun buildRequest(file: File, urlExternal: String): Request {
            val ifNoneMatch = readFileETag(file)

            val requestBuilder = Request.Builder().url(urlExternal.toHttpUrl())

            if (ifNoneMatch != null) {
                requestBuilder.addHeader("If-None-Match", ifNoneMatch)
            }

            return requestBuilder.build()
        }

        /**
         * Reads file eTag
         *
         * @param file file
         * @return eTag or null if not found
         */
        private fun readFileETag(file: File): String? {
            val fileMeta = if (file.exists()) readFileMeta(file) else null
            return fileMeta?.eTag
        }

        /**
         * Read file meta
         *
         * @param file file
         * @return file meta
         */
        private fun readFileMeta(file: File): FileMeta? {
            if (!file.exists()) {
                return null
            }

            val metaFile = File(file.parent, "${file.name}.meta")
            if (!metaFile.exists()) {
                return null
            }

            val json = metaFile.bufferedReader().use {
                it.readText()
            }

            return getFileMetaAdapter().fromJson(json)
        }

        /**
         * Writes file meta
         *
         * @param file meta
         * @param eTag eTag
         */
        private fun writeFileMeta(file: File, eTag: String?) {
            val metaFile = File(file.parent, "${file.name}.meta")
            if (metaFile.exists()) {
                metaFile.delete()
            }

            metaFile.parentFile?.mkdirs()
            metaFile.createNewFile()

            FileOutputStream(metaFile).use {
                it.write(getFileMetaAdapter().toJson(FileMeta(eTag = eTag)).toByteArray())
            }
        }

        /**
         * Returns JsonAdapter for FileMeta
         *
         * @return JsonAdapter for FileMeta
         */
        private fun getFileMetaAdapter(): JsonAdapter<FileMeta> {
            return getMoshi().adapter(FileMeta::class.java)
        }

        /**
         * Returns initialized Moshi instance
         *
         * @return initialized Moshi instance
         */
        private fun getMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

        /**
         * Calculates a md5 hash from given string
         *
         * @param str string
         * @return md5 hash
         */
        private fun md5(str: String): String {
            val hexString = StringBuilder()

            for (aMessageDigest in md5(str.toByteArray(StandardCharsets.UTF_8))) {
                hexString.append(Integer.toHexString(0xFF and aMessageDigest.toInt()).padStart(2, '0'))
            }

            return hexString.toString()
        }

        /**
         * Calculates a md5 hash from given bytes
         *
         * @param bytes bytes
         * @return md5 hash bytes
         */
        private fun md5(bytes: ByteArray): ByteArray {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(bytes)
            return digest.digest()
        }

        /**
         * Returns offlined image from URL as original or a scaled image if size exceeds 80MB
         *
         * @param offlineFile offlined file
         * @param maxImageWidth maximum width of returned image.
         * @param maxImageHeight maximum height of returned image.
         * @return Bitmap or null
         */
        private fun getFileBitmap(offlineFile: File?, maxImageWidth: Int, maxImageHeight: Int): Bitmap? {
            if (offlineFile == null || !offlineFile.exists()) {
                return null
            }

            val bitmap = BitmapFactory.decodeFile(offlineFile.absolutePath) ?: return null

            return if (bitmap.width > maxImageWidth || bitmap.height > maxImageHeight) {
                val scaleX = maxImageWidth.toFloat() / bitmap.width.toFloat()
                val scaleY = maxImageHeight.toFloat() / bitmap.height.toFloat()
                val scale = max(scaleX, scaleY)
                getScaledBitmap(bitmap = bitmap, scale = scale)
            } else {
                bitmap
            }
        }

        /**
         * Scales bitmap
         *
         * @param bitmap bitmap to be scaled
         * @param scale scaling factor
         * @return scaled bitmap
         */
        private fun getScaledBitmap(bitmap: Bitmap, scale: Float): Bitmap {
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
            bitmap.recycle()
            return resizedBitmap
        }
    }

}

/**
 * Private class for describing file meta JSON
 *
 * @param eTag file eTag
 */
private data class FileMeta(val eTag: String?)
