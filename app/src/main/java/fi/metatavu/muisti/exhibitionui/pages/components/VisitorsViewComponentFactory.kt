package fi.metatavu.muisti.exhibitionui.pages.components

import android.R
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.Observer
import fi.metatavu.muisti.exhibitionui.pages.PageViewLifecycleAdapter
import fi.metatavu.muisti.exhibitionui.visitors.VisibleVisitorsContainer
import fi.metatavu.muisti.exhibitionui.views.MuistiActivity

/**
 * Component factory for visitor view components
 */
class VisitorsViewComponentFactory : AbstractComponentFactory<ListView>() {
    override val name: String
        get() = "VisitorsView"

    override fun buildComponent(buildContext: ComponentBuildContext): ListView {
        val view = ListView(buildContext.context)
        setupView(buildContext, view)

        val parent = buildContext.parents.lastOrNull()
        view.layoutParams = getInitialLayoutParams(parent)

        buildContext.addLifecycleListener(object: PageViewLifecycleAdapter() {
            override fun onPageActivate(activity: MuistiActivity) {
                VisibleVisitorsContainer.getLiveVisibleVisitors().observe(activity, Observer {
                    visitors -> run {
                        val visitorNames = visitors
                            .sortedBy { it.lastName }
                            .sortedBy { it.firstName }
                            .map {"${it.lastName}, ${it.firstName}"}

                        view.adapter = ArrayAdapter(
                            buildContext.context,
                            R.layout.simple_list_item_1,
                            visitorNames
                        )
                    }
                })
            }

        })

        buildContext.pageLayoutView.properties.forEach {
            this.setProperty(buildContext, parent, view, it)
        }

        return view
    }

}