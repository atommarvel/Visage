package com.radiantmood.visage

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * In charge of taking in a canvas and drawing on it.
 */
class CanvasWatchFaceRenderer(private val context: Context) {
    private val digitalDateFormat = SimpleDateFormat("h:mma", Locale.getDefault())
    private var digitalTimeColor = Color.RED
    private val digitalTimePaint = Paint().apply {
        color = digitalTimeColor
        strokeWidth = DIGITAL_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        typeface = Typeface.createFromAsset(context.assets, "firacode.tff")
    }

    fun onDraw(canvas: Canvas, calendar: Calendar, surfaceSize: CalculatedRect, isAmbient: Boolean) {
        drawBg(canvas)
        drawWatchFace(canvas, calendar, surfaceSize, isAmbient)
    }

    private fun drawBg(canvas: Canvas) {
        canvas.drawColor(BG_COLOR)
    }

    private fun drawWatchFace(canvas: Canvas, calendar: Calendar, surfaceSize: CalculatedRect, isAmbient: Boolean) {
        val timeString = digitalDateFormat.format(calendar).lowercase()
//        canvas.drawText(timeString, surfaceSize.centerX, surfaceSize.centerY, digitalTimePaint)
        canvas.drawText("lol", surfaceSize.centerX, surfaceSize.centerY, digitalTimePaint)

        /*
         * Consider drawing less things in ambient and only update once a minute
         */
        if (!isAmbient) {

        }
    }

    companion object {
        private const val DIGITAL_STROKE_WIDTH = 5f
        private const val BG_COLOR = Color.BLACK
    }
}