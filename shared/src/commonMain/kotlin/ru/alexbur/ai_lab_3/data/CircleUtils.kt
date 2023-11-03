package ru.alexbur.ai_lab_3.data

import ru.alexbur.ai_lab_3.circleRadius
import ru.alexbur.ai_lab_3.padding

internal fun List<CircleColumn>.forEachCircle(action: (Circle) -> Unit) {
    forEach { column -> column.column.forEach { circle -> action(circle) } }
}

internal fun List<CircleColumn>.findCircle(predicate: (Circle) -> Boolean): Circle? {
    forEach { column ->
        val circle = column.column.find { circle -> predicate.invoke(circle) }
        if (circle != null) return circle
    }
    return null
}

fun calculateCircleCount(width: Float, height: Float): Pair<Int, Int> {
    val rawWidth = width - 2 * padding
    val rawHeight = height - 2 * padding

    val totalCirclesX = (rawWidth / (2 * circleRadius + padding)).toInt()
    val totalCirclesY = (rawHeight / (2 * circleRadius + padding)).toInt()
    return totalCirclesX to totalCirclesY
}