/*
 * Copyright (C) 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

// Information needed for complications.
// Creates bounds for the locations of both right and left complications. (This is the
// location from 0.0 - 1.0.)
// Both left and right complications use the same top and bottom bounds.
private const val COMPLICATION_SIZE = 0.2f

private const val LEFT_AND_RIGHT_COMPLICATIONS_TOP_BOUND = 0.4f
private const val LEFT_AND_RIGHT_COMPLICATIONS_BOTTOM_BOUND = LEFT_AND_RIGHT_COMPLICATIONS_TOP_BOUND + COMPLICATION_SIZE

private const val LEFT_COMPLICATION_LEFT_BOUND = 0.2f
private const val LEFT_COMPLICATION_RIGHT_BOUND = LEFT_COMPLICATION_LEFT_BOUND + COMPLICATION_SIZE

private const val RIGHT_COMPLICATION_LEFT_BOUND = 0.6f
private const val RIGHT_COMPLICATION_RIGHT_BOUND = RIGHT_COMPLICATION_LEFT_BOUND + COMPLICATION_SIZE

private const val TOP_COMPLICATION_TOP_BOUND = 0.05f
private const val TOP_COMPLICATION_BOTTOM_BOUND = TOP_COMPLICATION_TOP_BOUND + COMPLICATION_SIZE

private const val BOTTOM_COMPLICATION_BOTTOM_BOUND = 0.95f
private const val BOTTOM_COMPLICATION_TOP_BOUND = BOTTOM_COMPLICATION_BOTTOM_BOUND - COMPLICATION_SIZE

private const val TOP_AND_BOTTOM_COMPLICATION_LEFT_BOUND = 0.4f
private const val TOP_AND_BOTTOM_COMPLICATION_RIGHT_BOUND = TOP_AND_BOTTOM_COMPLICATION_LEFT_BOUND + COMPLICATION_SIZE

private const val DEFAULT_COMPLICATION_STYLE_DRAWABLE_ID = R.drawable.complication_red_style

// Unique IDs for each complication. The settings activity that supports allowing users
// to select their complication data provider requires numbers to be >= 0.
internal const val LEFT_COMPLICATION_ID = 100
internal const val RIGHT_COMPLICATION_ID = 101
internal const val TOP_COMPLICATION_ID = 102
internal const val BOTTOM_COMPLICATION_ID = 103
internal const val LONG_BOTTOM_COMPLICATION_ID = 104

/**
 * Represents the unique id associated with a complication and the complication types it supports.
 */
sealed class ComplicationConfig(val id: Int, val supportedTypes: List<ComplicationType>, val defaultDataSource: Int, val bounds: RectF) {
    class Left : ComplicationConfig(
        id = LEFT_COMPLICATION_ID,
        supportedTypes = listOf(
            ComplicationType.RANGED_VALUE,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SHORT_TEXT,
            ComplicationType.SMALL_IMAGE
        ),
        defaultDataSource = SystemDataSources.DATA_SOURCE_DAY_OF_WEEK,
        bounds = RectF(
            LEFT_COMPLICATION_LEFT_BOUND,
            LEFT_AND_RIGHT_COMPLICATIONS_TOP_BOUND,
            LEFT_COMPLICATION_RIGHT_BOUND,
            LEFT_AND_RIGHT_COMPLICATIONS_BOTTOM_BOUND
        )
    )

    class Right : ComplicationConfig(
        id = RIGHT_COMPLICATION_ID,
        supportedTypes = listOf(
            ComplicationType.RANGED_VALUE,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SHORT_TEXT,
            ComplicationType.SMALL_IMAGE
        ),
        defaultDataSource = SystemDataSources.DATA_SOURCE_STEP_COUNT,
        bounds = RectF(
            RIGHT_COMPLICATION_LEFT_BOUND,
            LEFT_AND_RIGHT_COMPLICATIONS_TOP_BOUND,
            RIGHT_COMPLICATION_RIGHT_BOUND,
            LEFT_AND_RIGHT_COMPLICATIONS_BOTTOM_BOUND
        )
    )

    class Top : ComplicationConfig(
        id = TOP_COMPLICATION_ID,
        supportedTypes = listOf(
            ComplicationType.RANGED_VALUE,
            ComplicationType.SHORT_TEXT
        ),
        defaultDataSource = SystemDataSources.DATA_SOURCE_NEXT_EVENT,
        bounds = RectF(
            TOP_AND_BOTTOM_COMPLICATION_LEFT_BOUND,
            TOP_COMPLICATION_TOP_BOUND,
            TOP_AND_BOTTOM_COMPLICATION_RIGHT_BOUND,
            TOP_COMPLICATION_BOTTOM_BOUND
        )
    )

    class Bottom : ComplicationConfig(
        id = BOTTOM_COMPLICATION_ID,
        supportedTypes = listOf(
            ComplicationType.RANGED_VALUE,
            ComplicationType.SHORT_TEXT
        ),
        defaultDataSource = SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
        bounds = RectF(
            TOP_AND_BOTTOM_COMPLICATION_LEFT_BOUND,
            BOTTOM_COMPLICATION_TOP_BOUND,
            TOP_AND_BOTTOM_COMPLICATION_RIGHT_BOUND,
            BOTTOM_COMPLICATION_BOTTOM_BOUND
        )
    )

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

//    val leftComplication = ComplicationConfig.Left().buildSlot(defaultCanvasComplicationFactory)
//    val rightComplication = ComplicationConfig.Right().buildSlot(defaultCanvasComplicationFactory)
    val topComplication = ComplicationConfig.Top().buildSlot(defaultCanvasComplicationFactory)
    val bottomComplication = ComplicationConfig.Bottom().buildSlot(defaultCanvasComplicationFactory)
    return ComplicationSlotsManager(
        listOf(ComplicationConfig.LongBottom().buildSlot(defaultCanvasComplicationFactory)),
        currentUserStyleRepository
    )
}
