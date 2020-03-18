package fi.metatavu.muisti.exhibitionui.pages

import android.content.Context
import android.view.View
import android.view.ViewGroup
import fi.metatavu.muisti.exhibitionui.pages.components.*
import fi.metatavu.muisti.exhibitionui.persistence.model.Layout
import fi.metatavu.muisti.exhibitionui.persistence.model.Page

/**
 * Page view factory
 */
class PageViewFactory {

    companion object {

        private val componentFactories = mutableListOf<ComponentFactory<*>>()

        init {
            componentFactories.add(TextViewComponentFactory())
            componentFactories.add(FlowTextViewComponentFactory())
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
         **@param context context
         * @param page page
         * @param layout page layout
         * @return build page view
         */
        fun buildPageView(context: Context, page: Page, layout: Layout) : PageView? {
            val lifecycleListeners = mutableListOf<PageViewLifecycleListener>()
            val buildContext = ComponentBuildContext(context = context, parents = arrayOf(), page = page, pageLayoutView = layout.data, lifecycleListeners = lifecycleListeners)
            val view = buildViewGroup(buildContext)
            view ?: return null
            return PageView(page = page, view = view, lifecycleListeners = lifecycleListeners, orientation = layout.orientation)
        }

        /**
         * Builds a view group
         *
         * @param buildContext component build context
         * @return build view group or null if failed
         */
        private fun buildViewGroup(buildContext: ComponentBuildContext) : View? {
            val factory = componentFactories.find { it.name == buildContext.pageLayoutView.widget }
            val root = factory?.buildComponent(buildContext)
            root?: return null

            if (root is ViewGroup) {
                buildContext.pageLayoutView.children.forEach {
                    val child = buildViewGroup(ComponentBuildContext(context = buildContext.context, parents = buildContext.parents.plus(root), page = buildContext.page, pageLayoutView = it, lifecycleListeners = buildContext.lifecycleListeners))
                    if (child != null) {
                        root.addView(child)
                    }
                }
            }

            return root
        }

    }

}