package fi.metatavu.muisti.exhibitionui.views

import android.app.ActivityOptions
import android.content.Intent
import android.transition.Explode
import android.transition.Fade
import android.transition.Transition
import android.util.Log
import android.view.View
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Pair
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import fi.metatavu.muisti.exhibitionui.ExhibitionUIApplication
import fi.metatavu.muisti.exhibitionui.R
import fi.metatavu.muisti.exhibitionui.pages.PageView
import kotlinx.android.synthetic.main.activity_page.*
import fi.metatavu.muisti.exhibitionui.session.VisitorSessionContainer
import java.util.*

/**
 * Muisti activity abstract class
 */
abstract class MuistiActivity : AppCompatActivity() {

    private var settingsClickCOunter = 0
    private val clickCounterHandler = Handler()

    /**
     * Starts listening for index button click
     *
     * @param button button
     */
    protected fun listenIndexButton(button: Button) {
        button.setOnClickListener{
            indexButtonClick()
        }
    }

    /**
     * Starts listening for settings button click
     *
     * @param button button
     */
    protected fun listenSettingsButton(button: Button) {
        button.setOnClickListener{
            settingsButtonClick()
        }
    }

    /**
     * Removes settings and index page listeners
     */
    protected fun removeSettingsAndIndexListeners() {
        clickCounterHandler.removeCallbacksAndMessages(null)
    }

abstract class MuistiActivity : AppCompatActivity() {

    var currentPageView: PageView? = null

    val transitionElements = mutableListOf<View>()

    /**
     * Opens a page view
     *
     * @param pageId page id
     */
    fun goToPage(pageId: UUID, sharedElements: List<View>? = null) {
        val intent = Intent(this, PageActivity::class.java).apply{
            putExtra("pageId", pageId.toString())
        }

        if (sharedElements != null) {
            val transitionElementPairs = sharedElements.map { Pair.create(it, it.transitionName ?: "") }.toTypedArray()
            val targetElements = transitionElementPairs.map { it.second }
            transitionElementPairs.forEach{
                Log.d(javaClass.name, "Linking transition element: " + it.first.tag + " and " + it.second)
            }
            intent.apply { putStringArrayListExtra("elements", ArrayList(targetElements))}
            val options = ActivityOptions
                .makeSceneTransitionAnimation(this, *transitionElementPairs)
            // start the new activity
            //options.toBundle()
            val oldView = currentPageView
            val alphaAnimation = AlphaAnimation(1f,0f) //AnimationUtils.loadAnimation(this,R.anim.basic_fade)!!
            alphaAnimation.duration = 1000
            alphaAnimation.fillAfter = false
            alphaAnimation.startOffset = 1
            alphaAnimation.repeatCount = 0
            oldView?.view?.startAnimation(alphaAnimation)
            //window.exitTransition. = Fade()
            //intent.flags += Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            overridePendingTransition(0, R.anim.basic_fade)
        } else {
/*
            with(window){
                exitTransition = Fade()
                enterTransition = Fade()
                enterTransition.duration = 5000
                exitTransition.duration = 5000
            }*/
            val oldView = currentPageView
            val alphaAnimation = AlphaAnimation(1f,0f) //AnimationUtils.loadAnimation(this,R.anim.basic_fade)!!
            alphaAnimation.duration = 1000
            alphaAnimation.fillAfter = false
            alphaAnimation.startOffset = 1
            oldView?.view?.startAnimation(alphaAnimation) ?: startActivity(intent)
            //intent.flags += Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            overridePendingTransition(0, R.anim.basic_fade)
            //window.exitTransition?.duration = 5000
        }

        //finish()
    }

    /**
     * Finds a view with tag
     *
     * @param tag tag
     * @return view or null if not found
     */
    protected fun findViewWithTag(tag: String?): View? {
        tag ?: return null
        return root.findViewWithTag(tag)
    }

    /**
     * Sets current activity into application
     *
     * @param activity activity
     */
    protected fun setCurrentActivity(activity: PageActivity?) {
        val application: ExhibitionUIApplication = this.applicationContext as ExhibitionUIApplication
        application.setCurrentActivity(activity)
    }

    /**
     * Starts a settings activity
     */
    protected fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }

    /**
     * Starts Main Activity
     */
    protected fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        VisitorSessionContainer.setVisitorSession(null)
        this.startActivity(intent)
    }

    /**
     * Handler for settings button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun settingsButtonClick() {
        clickCounterHandler.removeCallbacksAndMessages("settings")
        settingsClickCOunter += 1
        if (settingsClickCOunter > 4) {
            startSettingsActivity()
        } else {
            clickCounterHandler.postDelayed({
                settingsClickCOunter = 0
            }, "settings", 1000)
        }
    }

    /**
     * Handler for settings button click
     *
     * Increases settings click count and navigates to settings if it has been clicked 5 times.
     * Counter resets to zero after 1 sec
     */
    private fun indexButtonClick() {
        clickCounterHandler.removeCallbacksAndMessages("index")
        settingsClickCOunter += 1
        if (settingsClickCOunter > 4) {
            startMainActivity()
        } else {
            clickCounterHandler.postDelayed( {
                settingsClickCOunter = 0
            }, "index", 1000)
        }
    }
}
