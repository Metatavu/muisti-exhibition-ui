package fi.metatavu.muisti.exhibitionui.pages.components

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import fi.metatavu.muisti.api.client.models.ExhibitionPageLayoutViewProperty

interface ComponentFactory <T : View> {
    val name: String
    fun buildComponent(context: Context, properties : Array<ExhibitionPageLayoutViewProperty>) : T
}