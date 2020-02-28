package fi.metatavu.muisti.exhibitionui.pages

import android.view.View
import android.widget.LinearLayout

class PageLayoutContainer {
    companion object {
        val layoutMap = mutableMapOf<String, View>()
        fun get(id: String): View{
            return layoutMap[id]!!
        }
        fun set(id: String, view: View){
            layoutMap[id] = view
        }
    }
}