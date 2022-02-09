package com.radiantmood.visage

data class CalculatedRect(
    val width: Int,
    val height: Int,
) {
    val centerX = width / 2.0f
    val centerY = height / 2.0f
}