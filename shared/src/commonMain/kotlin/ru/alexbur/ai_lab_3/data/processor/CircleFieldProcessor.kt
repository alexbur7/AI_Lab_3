package ru.alexbur.ai_lab_3.data.processor

import ru.alexbur.ai_lab_3.MINIMUM_HAPPINESS_VALUE
import ru.alexbur.ai_lab_3.circleRadius
import ru.alexbur.ai_lab_3.data.Circle
import ru.alexbur.ai_lab_3.data.CircleColumn
import ru.alexbur.ai_lab_3.data.CircleOffset
import ru.alexbur.ai_lab_3.data.CircleState
import ru.alexbur.ai_lab_3.data.forEachCircle
import ru.alexbur.ai_lab_3.data.helper.CircleFieldHelper
import ru.alexbur.ai_lab_3.padding

class CircleFieldProcessor {

    private val circleField: CircleFieldHelper by lazy(LazyThreadSafetyMode.NONE) { CircleFieldHelper() }

    fun init(totalCirclesX: Int, totalCirclesY: Int): List<CircleColumn> {
        circleField.initFields(totalCirclesX, totalCirclesY)

        for (i in 0 until totalCirclesX) {
            for (j in 0 until totalCirclesY) {
                circleField.addCircleToFields(
                    circleRadius + padding + (i * (padding + 2 * circleRadius).toDouble()),
                    circleRadius + padding + (j * (padding + 2 * circleRadius).toDouble()),
                    CircleState.values().random(),
                    i,
                    j,
                )
            }
        }
        return circleField.initialField.toList()
    }

    fun moveUnhappyCircles(times: Int = 1): List<CircleColumn> {
        repeat(times) {
            val newField = mutableListOf<CircleColumn>().apply {
                addAll(circleField.modifiableField)
            }

            newField.forEachCircle { circle ->
                moveCircleIfUnhappy(circle)
            }
        }
        return circleField.modifiableField.toList()
    }

    fun moveTappedCircle(offset: CircleOffset): List<CircleColumn> {
        val circle = circleField.getTouchedCircle(offset)
        circle?.let {
            moveCircleIfUnhappy(circle)
        }
        return circleField.modifiableField.toList()
    }

    private fun moveCircleIfUnhappy(
        circle: Circle,
    ) {
        val newField = mutableListOf<CircleColumn>().apply {
            addAll(circleField.modifiableField)
        }

        val happiness = getCircleHappiness(circle)

        if (happiness < MINIMUM_HAPPINESS_VALUE) {
            val emptyCircle = newField.flatMap { it.column }.filter { it.state == CircleState.EMPTY }.random()

            swapElements(emptyCircle, circle)
        }
    }

    private fun getCircleHappiness(circle: Circle): Float {
        if (circle.state == CircleState.EMPTY) return DEFAULT_HAPPINESS
        var goodNeighbours = 0f

        val neighbours = circleField.getCircleNeighbours(circle)

        neighbours.forEach {
            if (circle.state == it.state) {
                goodNeighbours++
            }
        }

        return goodNeighbours / neighbours.size
    }

    private fun swapElements(
        emptyCircle: Circle,
        circle: Circle
    ) {
        val circleCoords = circleField.getCirclePositionInField(circle) ?: return
        val emptyCircleCoords = circleField.getCirclePositionInField(emptyCircle) ?: return

        val circleColumn = circleField.modifiableField[circleCoords.first]
        val emptyCircleColumn = circleField.modifiableField[emptyCircleCoords.first]

        if (circleCoords.first != emptyCircleCoords.first) {
            swapElementsOnDifferentColumns(
                circleCoords,
                circleColumn,
                emptyCircle,
                emptyCircleCoords,
                emptyCircleColumn,
                circle
            )
            return
        }
        swapElementsOnTheSameColumn(
            circleColumn,
            circleCoords,
            emptyCircle,
            emptyCircleCoords,
            circle
        )
    }

    private fun swapElementsOnDifferentColumns(
        circleCoords: Pair<Int, Int>,
        circleColumn: CircleColumn,
        emptyCircle: Circle,
        emptyCircleCoords: Pair<Int, Int>,
        emptyCircleColumn: CircleColumn,
        circle: Circle
    ) {
        circleField.modifiableField[circleCoords.first] =
            circleColumn.copy(
                column = circleColumn.column
                    .toMutableList()
                    .apply {
                        this[circleCoords.second] =
                            this[circleCoords.second].copy(
                                state = emptyCircle.state
                            )
                    }
            )

        circleField.modifiableField[emptyCircleCoords.first] =
            emptyCircleColumn.copy(
                column = emptyCircleColumn.column
                    .toMutableList()
                    .apply {
                        this[emptyCircleCoords.second] =
                            this[emptyCircleCoords.second].copy(
                                state = circle.state
                            )
                    }
            )
    }

    private fun swapElementsOnTheSameColumn(
        circleColumn: CircleColumn,
        circleCoords: Pair<Int, Int>,
        emptyCircle: Circle,
        emptyCircleCoords: Pair<Int, Int>,
        circle: Circle
    ) {
        val resultColumn = circleColumn.column.toMutableList().apply {
            this[circleCoords.second] = this[circleCoords.second].copy(
                state = emptyCircle.state
            )
            this[emptyCircleCoords.second] = this[emptyCircleCoords.second].copy(
                state = circle.state
            )
        }
        circleField.modifiableField[emptyCircleCoords.first] =
            circleField.modifiableField[emptyCircleCoords.first].copy(column = resultColumn)
    }

    companion object {
        private const val DEFAULT_HAPPINESS = 1f
    }
}