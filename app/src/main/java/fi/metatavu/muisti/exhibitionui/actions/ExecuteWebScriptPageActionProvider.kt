package fi.metatavu.muisti.exhibitionui.actions

import android.util.Log
import android.webkit.WebView
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventActionType
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventProperty
import fi.metatavu.muisti.exhibitionui.views.PageActivity

/**
 * Page action provider for execute web script action
 *
 * Action saves a user value
 *
 * @constructor constructor
 * @param properties event properties
 */
class ExecuteWebScriptPageActionProvider(properties: Array<ExhibitionPageEventProperty>): AbstractPageActionProvider(properties) {

    override fun performAction(pageActivity: PageActivity) {
        val webViewId = getPropertyString("webViewId")
        if (webViewId == null) {
            Log.d( javaClass.name,"webViewId required for execute web script action")
            return
        }

        val script = getPropertyString("script")
        if (script == null) {
            Log.d( javaClass.name,"script is required for execute web script action")
            return
        }

        val webView = pageActivity.findView(webViewId)
        if (webView == null) {
            Log.d( javaClass.name,"webView $webViewId does not exist")
            return
        }

        if (webView !is WebView) {
            Log.d( javaClass.name,"webView $webViewId is not a WebView")
            return
        }

        webView.evaluateJavascript(script, null)
    }

    override val action: ExhibitionPageEventActionType get() = ExhibitionPageEventActionType.executeWebScript

}