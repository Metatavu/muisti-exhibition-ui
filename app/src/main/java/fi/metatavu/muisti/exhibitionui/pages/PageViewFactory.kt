package fi.metatavu.muisti.exhibitionui.pages

import android.content.Context
import android.view.View
import android.view.ViewGroup
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.pages.components.*
import fi.metatavu.muisti.exhibitionui.persistence.model.Page

/**
 * Page view factory
 */
class PageViewFactory {

    companion object {

        private val componentFactories = mutableListOf<ComponentFactory<*>>()

        init {
            componentFactories.add(TextViewComponentFactory())
            componentFactories.add(ButtonComponentFactory())
            componentFactories.add(ImageViewComponentFactory())
            componentFactories.add(MediaViewComponentFactory())
            componentFactories.add(LinearLayoutComponentFactory())
            componentFactories.add(FrameLayoutComponentFactory())
            componentFactories.add(RelativeLayoutComponentFactory())
        }

        /**
         * Builds a page view
         *
         * @param context context
         * @param page page
         * @param pageView pageView
         * @return build page view
         */
        fun buildPageView(context: Context, page: Page, pageView: PageLayoutView) : PageView? {
            val activators = mutableListOf<PageViewActivator>()
            val view = buildViewGroup(context, arrayOf(), page.resources, pageView, activators)
            view ?: return null
            return PageView(page = page, view = view, activators = activators)
        }

        /**
         * Builds a view group
         *
         * @param context context
         * @param parents view parents
         * @param resources list of resources
         * @param pageLayoutView page layout view
         * @param activators list of activators
         * @return build view group or null if failed
         */
        private fun buildViewGroup(context: Context, parents: Array<View>, resources: Array<ExhibitionPageResource>, pageLayoutView: PageLayoutView, activators: MutableList<PageViewActivator>) : View? {
            val factory = componentFactories.find { it.name == pageLayoutView.widget }
            val root = factory?.buildComponent(context, parents, pageLayoutView, resources, activators)
            root?: return null

            if (root is ViewGroup) {
                pageLayoutView.children.forEach {
                    val child = buildViewGroup(context, parents.plus(root), resources, it, activators)
                    if (child!= null) {
                        root.addView(child)
                    }
                }
            }

            return root
        }

    }

}