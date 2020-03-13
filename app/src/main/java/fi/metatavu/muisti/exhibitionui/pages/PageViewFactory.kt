package fi.metatavu.muisti.exhibitionui.pages

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import fi.metatavu.muisti.api.client.models.ExhibitionPageResource
import fi.metatavu.muisti.api.client.models.PageLayoutView
import fi.metatavu.muisti.exhibitionui.pages.components.*

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
            componentFactories.add(LinearLayoutComponentFactory())
            componentFactories.add(FrameLayoutComponentFactory())
            componentFactories.add(RelativeLayoutComponentFactory())
        }

        /**
         * Builds a page view
         *
         * @param context context
         * @param resources list of resources
         * @param pageView pageView
         * @return
         */
        fun buildPageView(context: Context, resources: Array<ExhibitionPageResource>, pageView: PageLayoutView) : View? {
            return buildViewGroup(context, arrayOf(), resources, pageView)
        }

        /**
         * Builds a view group
         *
         * @param context context
         * @param parents view parents
         * @param resources list of resources
         * @param pageView page view
         * @return build view group or null if failed
         */
        private fun buildViewGroup(context: Context, parents: Array<View>, resources: Array<ExhibitionPageResource>, pageView: PageLayoutView) : View? {
            val factory = componentFactories.find { it.name == pageView.widget }
            val root = factory?.buildComponent(context, parents, pageView.id, resources, pageView.properties)
            root?: return null

            if (root is ViewGroup) {
                pageView.children.forEach {
                    val child = buildViewGroup(context, parents.plus(root), resources, it)
                    if (child!= null) {
                        root.addView(child)
                    }
                }
            }

            return root
        }

    }

}