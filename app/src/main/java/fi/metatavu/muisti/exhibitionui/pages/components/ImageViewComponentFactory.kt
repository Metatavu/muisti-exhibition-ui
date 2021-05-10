package fi.metatavu.muisti.exhibitionui.pages.components

import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import fi.metatavu.muisti.api.client.models.DeviceImageLoadStrategy
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.files.OfflineFileController
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleAdapter
import fi.metatavu.muisti.exhibitionui.pages.PageViewVisitorSessionAdapter
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity
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

        if (ExhibitionUIApplication.instance.deviceImageLoadStrategy == DeviceImageLoadStrategy.dISK) {
            buildContext.addLifecycleListener(ImageViewLifecycleListener(
                    imageView = imageView,
                    maxImageHeight = displayHeight,
                    maxImageWidth = displayWidth,
                    url = getUrl(getPropertyResourceData(buildContext = buildContext, propertyName = "src"))
            ))
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
                    getOfflineFile(url = url)
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

        if (ExhibitionUIApplication.instance.deviceImageLoadStrategy == DeviceImageLoadStrategy.dISK) {
            getOfflineImageFile(url = url) ?: return
        } else {
            val bitmap = getOfflineBitmap(url = url) ?: return
            imageView.setImageBitmap(bitmap)
        }
    }

    /**
     * Sets image src
     *
     * @param imageView image view component
     * @param url url or null
     */
    private fun updateImageSource(imageView: ImageView, url: URL?) {
        val bitmap = getOfflineBitmap(url = url)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        }
    }

}

/**
 * Image view lifecycle listener. Used when image load strategy is set to disk
 *
 * @param imageView image view
 * @param url image URL
 * @param maxImageWidth image maximum width
 * @param maxImageHeight image maximum height
 */
private class ImageViewLifecycleListener(private val imageView: ImageView, private val url: URL?, private val maxImageWidth: Int, private val maxImageHeight: Int): PageViewLifecycleAdapter() {

    override fun onPageActivate(activity: MuistiActivity) {
        url ?: return
        val offlineImageFile = OfflineFileController.getOfflineImageFile(url = url, maxImageWidth = maxImageWidth, maxImageHeight = maxImageHeight) ?: return
        val bitmap = BitmapFactory.decodeFile(offlineImageFile.absolutePath) ?: return
        imageView.setImageBitmap(bitmap)
    }

}
