package ru.alexbur.ai_lab_3.data

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
    val rawWidth = width - 2 * PADDING
    val rawHeight = height - 2 * PADDING

    val totalCirclesX = (rawWidth / (2 * CIRCLE_RADIUS + PADDING)).toInt()
    val totalCirclesY = (rawHeight / (2 * CIRCLE_RADIUS + PADDING)).toInt()
    return totalCirclesX to totalCirclesY
}