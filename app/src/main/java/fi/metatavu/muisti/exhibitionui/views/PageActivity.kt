package fi.metatavu.muisti.exhibitionui.views

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import fi.metatavu.muisti.api.client.models.ExhibitionPageEvent
import fi.metatavu.muisti.api.client.models.ExhibitionPageEventTrigger
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.actions.PageActionProvider
import fi.metatavu.muisti.exhibitionui.actions.PageActionProviderFactory
import fi.metatavu.muisti.exhibitionui.pages.PageView
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import kotlinx.android.synthetic.main.activity_page.*
import java.util.*
import fi.metatavu.muisti.api.client.models.MqttTriggerDeviceGroupEvent
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import android.view.KeyEvent
import android.widget.Button
import fi.metatavu.muisti.exhibitionui.R


/**
 * Activity for displaying pages from API
 */
class PageActivity : MuistiActivity() {

    private var currentPageView: PageView? = null
    private val handler: Handler = Handler()
    private val deviceGroupEvents = mutableMapOf<String, Array<ExhibitionPageEvent>>()
    private val keyDownListeners = mutableListOf<KeyCodeListener>()
    private val keyUpListeners = mutableListOf<KeyCodeListener>()

    private var settingsClickCOunter = 0
    private val settingsClickHandler = Handler()

    // TODO: Listen only device group messages
    private val mqttTriggerDeviceGroupEventListener = MqttTopicListener("${BuildConfig.MQTT_BASE_TOPIC}/events/deviceGroup/deviceGroupId", MqttTriggerDeviceGroupEvent::class.java) {
        val key = it.event
        if (key != null) {
            val events = deviceGroupEvents.get(key)
            if (events != null) {
                triggerEvents(events)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_page)
        setImmersiveMode()
        val pageId: String? = intent.getStringExtra("pageId")

        val pageView = PageViewContainer.getPageView(UUID.fromString(pageId))
        if (pageView == null) {
            // TODO: Handle error
            return
        }

        val button = findViewById<Button>(R.id.settings_button)
        button.setOnClickListener{
            settingsButtonClick()
        }

        pageView.view.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        pageView.view.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT

        this.openView(pageView)
    }

    override fun onResume() {
      setCurrentActivity(this)
      super.onResume()
    }

    override fun onPause() {
        setCurrentActivity(null)
        this.closeView()
        super.onPause()
    }

    override fun onDestroy() {
        this.closeView()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        triggerKeyListeners(keyCode, keyDown = true)
        super.onKeyDown(keyCode, event)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        triggerKeyListeners(keyCode, keyDown = false)
        super.onKeyUp(keyCode, event)
        return true
    }

    /**
     * Checks keycode listeners list and triggers the listeners with matching keyCode
     *
     * @param keyCode keycode of the button pressed
     * @param keyDown whether to trigger key down or key up listeners
     */
    private fun triggerKeyListeners(keyCode: Int, keyDown: Boolean){
        if(keyDown){
            keyDownListeners.forEach {
                if(it.keyCode == keyCode){
                    it.listener()
                }
            }
        } else {
            keyUpListeners.forEach {
                if(it.keyCode == keyCode){
                    it.listener()
                }
            }
        }
    }

    /**
     * Opens a view
     *
     * Method adds page view as a child of page activity view and
     * applies page triggers into the page
     *
     * @param pageView
     */
    private fun openView(pageView: PageView) {
        currentPageView = pageView
        this.root.addView(pageView.view)
        pageView.lifecycleListeners.forEach { it.onPageActivate(this) }
        applyEventTriggers(pageView.page.eventTriggers)
        requestedOrientation = pageView.orientation
        MqttClientController.addListener(mqttTriggerDeviceGroupEventListener)
    }

    /**
     * Closes a view
     *
     * Method cancels all pending scheduled events and
     * releases page view from this page activity instance
     */
    private fun closeView() {
        MqttClientController.removeListener(mqttTriggerDeviceGroupEventListener)
        handler.removeCallbacksAndMessages(null)
        settingsClickHandler.removeCallbacksAndMessages(null)
        val currentView = currentPageView?.view
        currentPageView?.lifecycleListeners?.forEach { it.onPageDeactivate(this) }

        if (currentView != null) {
            this.root.removeView(currentView)
        }
    }

    /**
     * Applies event triggers
     *
     * @param eventTriggers event triggers to be applied
     */
    private fun applyEventTriggers(eventTriggers: Array<ExhibitionPageEventTrigger>) {
        deviceGroupEvents.clear()
        eventTriggers.map(this::applyEventTrigger)
    }

    /**
     * Applies an event trigger
     *
     * @param eventTrigger event trigger to be applied
     */
    private fun applyEventTrigger(eventTrigger: ExhibitionPageEventTrigger) {
        val events = eventTrigger.events
        events ?: return

        val delay: Long = eventTrigger.delay ?: 0
        if (delay > 0) {
            scheduleTimedEvent(delay, events)
        }

        val clickViewId = eventTrigger.clickViewId

        if (clickViewId != null) {
            bindClickEventListener(clickViewId, events)
        }

        val deviceGroupEvent = eventTrigger.deviceGroupEvent

        if (deviceGroupEvent != null) {
            val deviceGroupEventList = deviceGroupEvents.get(deviceGroupEvent) ?: arrayOf<ExhibitionPageEvent>()
            deviceGroupEvents.put(deviceGroupEvent, deviceGroupEventList.plus(events))
        }

        val keyCodeUp = eventTrigger.keyUp
        val keyCodeDown = eventTrigger.keyDown

        if(keyCodeDown != null) {
            bindKeyCodeEventListener(keyCodeDown, events, true)
        }
        if(keyCodeUp != null) {
            bindKeyCodeEventListener(keyCodeUp, events, false)
        }
    }

    /**
     * Schedules timed events
     *
     * @param delay the delay (in milliseconds) until events will be triggered
     * @param events events to be triggered
     */
    private fun scheduleTimedEvent(delay: Long, events: Array<ExhibitionPageEvent>) {
        handler.postDelayed({
            triggerEvents(events)
        }, delay)
    }

    /**
     * Binds event trigger to a view click
     *
     * @param clickViewId client view id
     * @param events events to be triggered on click
     */
    private fun bindClickEventListener(clickViewId: String, events: Array<ExhibitionPageEvent>) {
        val clickView = this.findViewWithTag(clickViewId)

        if (clickView == null) {
            Log.d(this.javaClass.name, "Failed to locate view by id $clickViewId")
        } else {
            clickView.setOnClickListener {
                triggerEvents(events)
            }
        }
    }

    /**
     * Binds event trigger to a keycode
     *
     * @param keyCodeString keycode to trigger events with
     * @param events events to be triggered by keycode
     * @param keyDown whether to bind to key down or key up
     */
    private fun bindKeyCodeEventListener(keyCodeString: String, events: Array<ExhibitionPageEvent>, keyDown: Boolean) {
        val listener = { triggerEvents(events) }
        val keyCode = KeyEvent.keyCodeFromString("KEYCODE_${keyCodeString.toUpperCase()}")
        if(keyDown){
            keyDownListeners.add(KeyCodeListener(keyCode, listener))
        } else {
            keyUpListeners.add(KeyCodeListener(keyCode, listener))
        }
    }

    /**
     * Triggers events
     *
     * @param events events to be triggered
     */
    private fun triggerEvents(events: Array<ExhibitionPageEvent>) {
        events.forEach(this::triggerEvent)
    }

    /**
     * Triggers an event
     *
     * @param event event to be triggered
     */
    private fun triggerEvent(event: ExhibitionPageEvent) {
        val properties = event.properties
        val provider: PageActionProvider? = PageActionProviderFactory.buildProvider(event.action, properties)
        if (provider == null) {
            Log.d(this.javaClass.name, "Could not find page action provider for ${event.action}")
            return
        }

        provider.performAction(this)
    }

    /**
     * Finds a view with tag
     *
     * @param tag tag
     * @return view or null if not found
     */
    private fun findViewWithTag(tag: String?): View? {
        tag ?: return null
        return root.findViewWithTag(tag)
    }

    /**
     * Changes activity to use immersive mode
     */
    private fun setImmersiveMode() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    /**
     * Handler for settings button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun settingsButtonClick() {
        settingsClickHandler.removeCallbacksAndMessages(null)
        settingsClickCOunter += 1
        if(settingsClickCOunter > 4){
            startSettingsActivity()
        } else {
            settingsClickHandler.postDelayed({
                settingsClickCOunter = 0
            }, 1000)
        }
    }
}

/**
 * Keycode Listener class for triggering events with key presses
 *
 * @param keyCode Keycode that triggers the listener
 * @param listener listener to trigger with keycode
 */
class KeyCodeListener(var keyCode: Int, var listener: () -> Unit)