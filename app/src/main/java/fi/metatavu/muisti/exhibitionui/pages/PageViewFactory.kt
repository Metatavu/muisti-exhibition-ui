package fi.metatavu.muisti.exhibitionui.pages

import android.content.Context
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
            componentFactories.add(FlowTextViewComponentFactory())
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
            val root = factory?.buildComponent(context, arrayOf(), pageView.id, resources, pageView.properties)
            root?: return null
            val childParents = parents.plus(root)

            if (root is ViewGroup) {
                pageView.children.forEach {
                    if (it.children.isNotEmpty()) {
                        val view = buildViewGroup(context, childParents, resources, it)
                        if (view != null) {
                            root.addView(view)
                        }
                    } else {
                        val childView = buildView(context, childParents, resources, it)
                        if (childView != null) {
                            root.addView(childView)
                        }
                    }
                }
            }

            return root
        }

        /**
         * Builds a view
         *
         * @param context context
         * @param parents view parents
         * @param resources resources
         * @param pageView page view
         * @return build view or null if failed
         */
        private fun buildView(context: Context, parents: Array<View>, resources: Array<ExhibitionPageResource>, pageView: PageLayoutView) : View? {
            val componentFactory = componentFactories.firstOrNull { it.name == pageView.widget }
            return componentFactory?.buildComponent(context, parents, pageView.id, resources, pageView.properties)
        }

    }

}