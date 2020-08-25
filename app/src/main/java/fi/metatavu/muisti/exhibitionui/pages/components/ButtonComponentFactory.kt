package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Button
import fi.metatavu.muisti.api.client.models.PageLayoutViewProperty
import fi.metatavu.muisti.api.client.models.VisitorSession
import fi.metatavu.muisti.exhibitionui.pages.PageViewVisitorSessionListener
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import fi.metatavu.muisti.exhibitionui.views.PageActivity
import java.net.URL

/**
 * Component factory for buttons
 */
class ButtonComponentFactory : AbstractComponentFactory<Button>() {

    override val name: String
        get() = "Button"

    override fun buildComponent(buildContext: ComponentBuildContext): Button {
        val button = Button(buildContext.context)
        setId(button, buildContext.pageLayoutView)

        val parent = buildContext.parents.lastOrNull()
        button.layoutParams = getInitialLayoutParams(parent)

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, button, it )
        }

        buildContext.addVisitorSessionListener(object : PageViewVisitorSessionListener {

            override suspend fun prepareVisitorSessionChange(pageActivity: PageActivity, visitorSession: VisitorSession) {
                prepareBackgroundImage(visitorSession)
            }

            override fun performVisitorSessionChange(pageActivity: PageActivity, visitorSession: VisitorSession) {
                updateBackgroundImage(visitorSession)
            }

            /**
             * Prepares background image for scripted resources
             */
            private fun prepareBackgroundImage(visitorSession: VisitorSession) {
                val url = getUrl(getScriptedResource(buildContext,  visitorSession, "backgroundImage", false))
                if (url != null) {
                    getResourceOfflineFile(buildContext, url)
                }
            }

            /**
             * Updates background image for scripted resources
             */
            private fun updateBackgroundImage(visitorSession: VisitorSession) {
                val url = getUrl(getScriptedResource(buildContext, visitorSession,"backgroundImage", false))
                if (url != null) {
                    setBackgroundImage(buildContext, button, url)
                }
            }

        })

        return button
    }

    override fun setProperty(buildContext: ComponentBuildContext, parent: View?, view: Button, property: PageLayoutViewProperty) {
        try {
            when(property.name) {
                "width" -> setWidth(view, property.value)
                "height" -> setHeight(view, property.value)
                "textColor" -> setTextColor(view, property.value)
                "textSize" -> setTextSize(view, property)
                "text" ->setText(buildContext, view, property)
                "textStyle" -> setTextStyle(view, property)
                "allCaps" -> setAllCaps(view, property.value)
                "gravity" -> setGravity(view, property.value)
                "backgroundImage" -> setBackgroundImage(buildContext, view, property.value)
                else -> super.setProperty(buildContext, parent, view, property)
            }
        } catch (e: Exception) {
            Log.d(ButtonComponentFactory::javaClass.name, "Failed to set property ${property.name} to ${property.value}}", e)
        }
    }


    /**
     * Sets text color
     *
     * @param view view
     * @param value value
     */
    private fun setTextColor(view: Button, value: String) {
        val color = getColor(value)
        color ?: return
        view.setTextColor(color)
    }

    /**
     * Sets background image.
     *
     * On scripted resources this method is does not do anything because scripted resources are
     * handled by visitor session change events
     *
     * @param buildContext build context
     * @param view view component
     * @param value value
     */
    private fun setBackgroundImage(buildContext: ComponentBuildContext, view: Button, value: String?) {
        val resource = getResource(buildContext, value)
        val scripted = resource?.scripted ?: false

        if (scripted) {
            return
        }

        val resourceData = resource?.data

        val url = getUrl(resourceData ?: value)
        url ?: return

        setBackgroundImage(buildContext = buildContext, view = view, url = url)
    }

    /**
     * Sets background image
     *
     * @param buildContext build context
     * @param view view component
     * @param url url
     */
    private fun setBackgroundImage(buildContext: ComponentBuildContext, view: Button, url: URL?) {
        val offlineFile = getResourceOfflineFile(buildContext = buildContext, url = url)
        offlineFile ?: return

        val bitmap = BitmapFactory.decodeFile(offlineFile.absolutePath)
        if (bitmap != null) {
            view.background = BitmapDrawable(Resources.getSystem(), bitmap)
        }
    }

    /**
     * Sets text size from property
     *
     * @param button button
     * @param property property
     */
    private fun setTextSize(button: Button, property: PageLayoutViewProperty) {
        val px = stringToPx(property.value)
        px ?: return
        button.textSize = px
    }

    /**
     * Sets text styles
     *
     * @param property
     * @param button
     */
    private fun setTextStyle(button: Button, property: PageLayoutViewProperty) {
        when (property.value) {
            "bold" -> button.typeface = Typeface.DEFAULT_BOLD
            "normal" -> button.typeface = Typeface.DEFAULT
        }
    }

    /**
     * Sets button width
     *
     * @param button button
     * @param value value
     */
    private fun setWidth(button: Button, value: String?) {
        val px = stringToPx(value)
        px ?: return
        button.layoutParams.width = px.toInt()
    }

    /**
     * Sets button height
     *
     * @param button button
     * @param value value
     */
    private fun setHeight(button: Button, value: String) {
        val px = stringToPx(value)
        px ?: return
        button.layoutParams.height = px.toInt()
    }

    /**
     * Sets button text from property
     *
     * @param buildContext build context
     * @param button button view component
     * @param property property
     */
    private fun setText(buildContext: ComponentBuildContext, button: Button, property: PageLayoutViewProperty) {
        val text = getResourceData(buildContext, property.value)
        button.text = text ?: property.value
    }

    /**
     * Sets button height
     *
     * @param button button
     * @param value value
     */
    private fun setAllCaps(button: Button, value: String) {
        if ("false" == value) {
            button.transformationMethod = null
        }
    }

    /**
     * Sets gravity property
     *
     * @param view view component
     * @param value value
     */
    private fun setGravity(view: Button, value: String) {
        val gravity = parseGravity(value)
        gravity ?: return
        view.gravity = gravity
    }
}