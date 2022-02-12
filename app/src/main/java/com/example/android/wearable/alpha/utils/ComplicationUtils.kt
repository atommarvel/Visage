package com.example.android.wearable.alpha.utils

import android.content.Context
import android.graphics.RectF
import androidx.wear.watchface.CanvasComplicationFactory
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.complications.ComplicationSlotBounds
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import com.example.android.wearable.alpha.R

private const val DEFAULT_COMPLICATION_STYLE_DRAWABLE_ID = R.drawable.complication_red_style

// Unique IDs for each complication. The settings activity that supports allowing users
// to select their complication data provider requires numbers to be >= 0.
internal const val LONG_BOTTOM_COMPLICATION_ID = 104

/**
 * Represents the unique id associated with a complication and the complication types it supports.
 * @param bounds for location placement of complication (location goes from 0.0 - 1.0)
 */
sealed class ComplicationConfig(val id: Int, val supportedTypes: List<ComplicationType>, val defaultDataSource: Int, val bounds: RectF) {

    class LongBottom : ComplicationConfig(
        id = LONG_BOTTOM_COMPLICATION_ID,
        supportedTypes = listOf(ComplicationType.LONG_TEXT),
        defaultDataSource = SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
        bounds = RectF(
            0.05f,
            0.55f,
            0.95f,
            0.75f
        )
    )

    fun buildSlot(canvasComplicationFactory: CanvasComplicationFactory) =
        ComplicationSlot.createRoundRectComplicationSlotBuilder(
            id = id,
            canvasComplicationFactory = canvasComplicationFactory,
            supportedTypes = supportedTypes,
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(defaultDataSource),
            bounds = ComplicationSlotBounds(bounds)
        ).setDefaultDataSourceType(ComplicationType.SHORT_TEXT)
            .build()
}

// Utility function that initializes default complication slots (left and right).
fun createComplicationSlotManager(
    context: Context,
    currentUserStyleRepository: CurrentUserStyleRepository,
    drawableId: Int = DEFAULT_COMPLICATION_STYLE_DRAWABLE_ID
): ComplicationSlotsManager {

    val defaultCanvasComplicationFactory =
        CanvasComplicationFactory { watchState, listener ->
            CanvasComplicationDrawable(
                ComplicationDrawable.getDrawable(context, drawableId)!!,
                watchState,
                listener
            )
        }

    return ComplicationSlotsManager(
        listOf(ComplicationConfig.LongBottom().buildSlot(defaultCanvasComplicationFactory)),
        currentUserStyleRepository
    )
}
