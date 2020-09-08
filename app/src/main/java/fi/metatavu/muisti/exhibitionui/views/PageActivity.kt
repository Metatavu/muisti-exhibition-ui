package fi.metatavu.muisti.exhibitionui.views

import android.animation.TimeInterpolator
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.transition.Fade
import android.transition.Visibility
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import fi.metatavu.muisti.exhibitionui.actions.PageActionProvider
import fi.metatavu.muisti.exhibitionui.actions.PageActionProviderFactory
import fi.metatavu.muisti.exhibitionui.pages.PageView
import fi.metatavu.muisti.exhibitionui.pages.PageViewContainer
import kotlinx.android.synthetic.main.activity_page.*
import java.util.*
import fi.metatavu.muisti.exhibitionui.BuildConfig
import fi.metatavu.muisti.exhibitionui.mqtt.MqttClientController
import fi.metatavu.muisti.exhibitionui.mqtt.MqttTopicListener
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.view.animation.*
import fi.metatavu.muisti.api.client.models.*
import fi.metatavu.muisti.api.client.models.Animation
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication

/**
 * Activity for displaying pages from API
 */
class PageActivity : MuistiActivity() {

    private val handler: Handler = Handler()
    private val deviceGroupEvents = mutableMapOf<String, Array<ExhibitionPageEvent>>()
    private val keyDownListeners = mutableListOf<KeyCodeListener>()
    private val keyUpListeners = mutableListOf<KeyCodeListener>()

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
        val pageId: String? = intent.getStringExtra("pageId")

        val pageView = PageViewContainer.getPageView(UUID.fromString(pageId))
        if (pageView == null) {
            // TODO: Handle error
            return
        }

        super.onCreate(savedInstanceState)

        val context = this

        val pageEnterTransitions = pageView.page.enterTransitions.mapNotNull { context.getPageAnimation(it.transition, Visibility.MODE_IN) }
        val pageExitTransitions = pageView.page.exitTransitions.mapNotNull { context.getPageAnimation(it.transition, Visibility.MODE_OUT) }

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            allowEnterTransitionOverlap = true
            enterTransition = if(pageEnterTransitions.isNotEmpty()){
                pageEnterTransitions[0]
            } else {
                null
            }
            exitTransition  = if(pageExitTransitions.isNotEmpty()){
                pageExitTransitions[0]
            } else {
                null
            }
        }
        supportActionBar?.hide()

        setContentView(R.layout.activity_page)
        setImmersiveMode()

        listenSettingsButton(settings_button)
        listenIndexButton(index_page_button)

        pageView.view.layoutParams?.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        pageView.view.layoutParams?.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        this.openView(pageView)
    }

    override fun onResume() {
        setCurrentActivity(this)
        super.onResume()
        currentPageView?.lifecycleListeners?.forEach { it.onResume() }
    }

    override fun onPause() {
        if(getCurrentActivity() == this){
            setCurrentActivity(null)
        }
        this.closeView()
        super.onPause()
        currentPageView?.lifecycleListeners?.forEach { it.onPause() }
    }

    override fun onDestroy() {
        this.releaseView(currentPageView?.view)
        super.onDestroy()
        currentPageView?.lifecycleListeners?.forEach { it.onDestroy() }
    }

    override fun finish() {
        disableClickEvents(currentPageView?.page?.eventTriggers)
        super.finish()
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

    override fun onLowMemory() {
        super.onLowMemory()
        currentPageView?.lifecycleListeners?.forEach { it.onLowMemory() }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        currentPageView?.lifecycleListeners?.forEach { it.onSaveInstanceState(outState) }
    }

    /**
     * Manually triggers a visitor session change
     */
    fun triggerVisitorSessionChange() {
        val pageView = currentPageView
        if (pageView != null) {
            triggerVisitorSessionChange(pageView = pageView)
        }
    }

    /**
     * Finds a view with tag
     *
     * @param tag tag
     * @return view or null if not found
     */
    fun findView(tag: String?): View? {
        return super.findViewWithTag(tag)
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
        releaseView(pageView.view)
        currentPageView = pageView
        this.root.addView(pageView.view)
        setSharedElementTransitions(pageView.page.enterTransitions)
        setSharedElementTransitions(pageView.page.exitTransitions)

        if (ExhibitionUIApplication.instance.forcedPortraitMode != true) {
            requestedOrientation = pageView.orientation
        }

        MqttClientController.addListener(mqttTriggerDeviceGroupEventListener)
        pageView.lifecycleListeners.forEach { it.onPageActivate(this) }
        triggerVisitorSessionChange(pageView)
        applyEventTriggers(pageView.page.eventTriggers)
    }

    /**
     * Closes a view
     *
     * Method cancels all pending scheduled events
     */
    private fun closeView() {
        MqttClientController.removeListener(mqttTriggerDeviceGroupEventListener)
        handler.removeCallbacksAndMessages(null)
        currentPageView?.lifecycleListeners?.forEach { it.onPageDeactivate(this) }
        removeSettingsAndIndexListeners()
    }

    /**
     * Removes all children from the specified views parent
     *
     * @param view view from which parent all children will be removed
     */
    private fun releaseView(view: View?) {
        view ?: return
        val parent = view.parent
        if (parent is ViewGroup){
            parent.removeAllViews()
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
     * Sets shared element transition target names for all elements
     *
     * @param transitions Array of ExhibitionPageTransitions
     */
    private fun setSharedElementTransitions(transitions: Array<ExhibitionPageTransition>) {
        transitions.forEach{
            it.options?.morph?.views?.map(this::setTransitionTargetName)
        }
    }

    /**
     * Sets transition target name for transition morph
     *
     * @param morphPair ExhibitionPageTransitionOptionsMorphView
     */
    private fun setTransitionTargetName(morphPair : ExhibitionPageTransitionOptionsMorphView) {
        val view = findViewWithTag(morphPair.sourceId) ?: return
        view.transitionName = morphPair.targetId
        transitionElements.add(view)
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
            val deviceGroupEventList = deviceGroupEvents.get(deviceGroupEvent) ?: arrayOf()
            deviceGroupEvents[deviceGroupEvent] = deviceGroupEventList.plus(events)
        }

        val keyCodeUp = eventTrigger.keyUp
        val keyCodeDown = eventTrigger.keyDown

        if (keyCodeDown != null) {
            bindKeyCodeEventListener(keyCodeDown, events, true)
        }

        if (keyCodeUp != null) {
            bindKeyCodeEventListener(keyCodeUp, events, false)
        }
    }

    /**
     * Disables clickable attribute from views attached to event triggers.
     *
     * @param eventTriggers event triggers to disable click views from
     */
    private fun disableClickEvents(eventTriggers: Array<ExhibitionPageEventTrigger>?) {
        eventTriggers?.forEach {
            val clickViewId = it.clickViewId
            if (clickViewId != null) {
                findViewWithTag(clickViewId)?.isClickable = false
            }
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
     * Gets interpolator based on animation time iterpolation enum
     *
     * @param interpolator Animation time interpolation enum
     * @return Android TimeInterpolator
     */
    private fun getInterpolator(interpolator: AnimationTimeInterpolation): TimeInterpolator {
        return when (interpolator) {
            AnimationTimeInterpolation.accelerate -> AccelerateInterpolator()
            AnimationTimeInterpolation.acceleratedecelerate -> AccelerateDecelerateInterpolator()
            AnimationTimeInterpolation.anticipate -> AnticipateInterpolator()
            AnimationTimeInterpolation.anticipateovershoot -> AnticipateOvershootInterpolator()
            AnimationTimeInterpolation.bounce -> BounceInterpolator()
            AnimationTimeInterpolation.decelerate -> DecelerateInterpolator()
            AnimationTimeInterpolation.linear -> LinearInterpolator()
            AnimationTimeInterpolation.overshoot -> OvershootInterpolator()
        }
    }

    /**
     * Returns android transition, Only supports fade.
     *
     * @param transition transition to parse
     * @param mode Visibility mode in or mode out based on
     * @return Android Transition or null if no supported animation can be generated.
     */
    private fun getPageAnimation(transition: Transition, mode: Int) : android.transition.Transition? {
        return if(transition.animation == Animation.fade) {
            val fade = Fade()
            fade.duration = transition.duration.toLong()
            fade.interpolator = getInterpolator(transition.timeInterpolation)
            fade.mode = mode
            fade
        } else {
            null
        }
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
     * Triggers a visitor session change
     *
     * @param pageView page view
     */
    private fun triggerVisitorSessionChange(pageView: PageView) {
        GlobalScope.launch {
            prepareVisitorSessionChange(pageView)

            runOnUiThread {
                performVisitorSessionChange(pageView)
            }
        }
    }

    /**
     * Executes prepare for visitor session change listeners
     *
     * @param pageView page view
     */
    private suspend fun prepareVisitorSessionChange(pageView: PageView) {
        val pageActivity = this
        val visitorSession = VisitorSessionContainer.getVisitorSession()
        if (visitorSession != null) {
            pageView.visitorSessionListeners.forEach {
                it.prepareVisitorSessionChange(
                    pageActivity = pageActivity,
                    visitorSession = visitorSession
                )
            }
        }
    }

    /**
     * Executes perform for visitor session change listeners
     *
     * @param pageView page view
     */
    private fun performVisitorSessionChange(pageView: PageView) {
        val pageActivity = this
        val visitorSession = VisitorSessionContainer.getVisitorSession()
        if (visitorSession != null) {
            pageView.visitorSessionListeners.forEach {
                it.performVisitorSessionChange(
                    pageActivity = pageActivity,
                    visitorSession = visitorSession
                )
            }
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
