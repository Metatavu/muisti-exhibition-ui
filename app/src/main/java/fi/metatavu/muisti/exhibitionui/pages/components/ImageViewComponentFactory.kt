package fi.metatavu.muisti.exhibitionui.pages.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.pages.PageViewVisitorSessionAdapter
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import java.net.URL

/**
 * Component factory for image components
 */
class ImageViewComponentFactory : AbstractComponentFactory<ImageView>() {
    override val name: String
        get() = "ImageView"

    override fun buildComponent(buildContext: ComponentBuildContext): ImageView {
        val imageView = ImageView(buildContext.context)
        setupView(buildContext, imageView)

        val parent = buildContext.parents.lastOrNull()
        imageView.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, imageView, it)
        }

        buildContext.addVisitorSessionListener(object : PageViewVisitorSessionAdapter() {
            override suspend fun prepareVisitorSessionChange(pageActivity: PageActivity, visitorSession: VisitorSession) {
                prepareImage(visitorSession)
            }

            override fun performVisitorSessionChange(pageActivity: PageActivity, visitorSession: VisitorSession) {
                updateImage(visitorSession)
            }

            /**
             * Prepares image for scripted resources
             *
             * @param visitorSession Visitor session that triggered the preparation
             */
            private fun prepareImage(visitorSession: VisitorSession) {
                val url = getUrl(getScriptedResource(buildContext,  visitorSession, "src", false))
                if (url != null) {
                    getResourceOfflineFile(url = url)
                }
            }

            /**
             * Updates image for scripted resources
             *
             * @param visitorSession Visitor session that triggered the update
             */
            private fun updateImage(visitorSession: VisitorSession) {
                val url = getUrl(getScriptedResource(buildContext, visitorSession,"src", false))
                if (url != null) {
                    updateImageSource(imageView = imageView, url = url)
                }
            }
        })

        return imageView
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: ImageView, property: PageLayoutViewProperty) {
        try {
            when (property.name) {
                "src" -> setSrc(buildContext, view, property.value)
                else -> super.setProperty(buildContext, parent, view, property)
            }


        } catch (e: Exception) {
            Log.d(ImageViewComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }

    /**
     * Sets a image src
     *
     * @param buildContext build context
     * @param imageView image view component
     * @param value value
     */
    private fun setSrc(buildContext: ComponentBuildContext, imageView: ImageView, value: String?) {
        val resource = getResourceData(buildContext, value)
        val url = getUrl(resource ?: value)
        url ?: return
        val bmp = getScaledImage(url)
        imageView.setImageBitmap(bmp)
    }

    /**
     * Sets image src
     *
     * @param imageView image view component
     * @param url url or null
     */
    private fun updateImageSource(imageView: ImageView, url: URL?) {
        val offlineFile = getResourceOfflineFile(url = url)
        offlineFile ?: return

        val bitmap = BitmapFactory.decodeFile(offlineFile.absolutePath)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        }
    }

    /**
     * Returns image from URL as original or a scaled image if size exceeds 80MB
     *
     * @param url URL to get image from
     * @return Bitmap or null if decoding fails
     */
    private fun getScaledImage(url: URL): Bitmap? {
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        return if (bmp.byteCount > 80000000) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 2
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options)
        } else {
            bmp
        }
    }
}
