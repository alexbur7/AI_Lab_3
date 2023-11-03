package ru.alexbur.ai_lab_3.data

import kotlin.math.pow
import kotlin.math.sqrt

data class Circle(
    val centerX: Double = -1.0,
    val centerY: Double = -1.0,
    val state: CircleState = CircleState.EMPTY,
) {

    fun isPointInsideCircle(x: Double, y: Double, circleRadius: Float): Boolean {
        return getDistanceFromPoint(x, y) <= circleRadius
    }

    private fun getDistanceFromPoint(x: Double, y: Double): Double {
        return sqrt((centerX - x).pow(2.0) + (centerY - y).pow(2.0))
    }
}