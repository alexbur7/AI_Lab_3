package ru.alexbur.ai_lab_3.data.helper

import ru.alexbur.ai_lab_3.data.Circle
import ru.alexbur.ai_lab_3.data.CircleColumn
import ru.alexbur.ai_lab_3.data.CircleOffset
import ru.alexbur.ai_lab_3.data.CircleState
import ru.alexbur.ai_lab_3.data.findCircle

class CircleFieldHelper(private val circleRadius: Float) {

    val initialField = mutableListOf<CircleColumn>()
    val modifiableField = mutableListOf<CircleColumn>()

    fun initFields(x: Int, y: Int) {
        if (initialField.isEmpty()) {
            initialField.addAll(
                MutableList(x) { CircleColumn(MutableList(y) { Circle() }) }
            )
            modifiableField.addAll(
                MutableList(x) { CircleColumn(MutableList(y) { Circle() }) }
            )
        }
    }

    fun addCircleToFields(
        x: Double,
        y: Double,
        state: CircleState,
        rowIndex: Int,
        columnIndex: Int
    ) {
        addCircleToField(x, y, state, rowIndex, columnIndex, initialField)
        addCircleToField(x, y, state, rowIndex, columnIndex, modifiableField)
    }

    fun getTouchedCircle(offset: CircleOffset): Circle? {
        return modifiableField.findCircle { it.isPointInsideCircle(offset.x, offset.y, circleRadius) }
    }

    fun getCirclePositionInField(circle: Circle?): Pair<Int, Int>? {
        var rowIndex = -1
        var columnIndex = -1
        modifiableField.forEachIndexed { index, circleRow ->
            if (circleRow.column.contains(circle)) {
                rowIndex = index
                columnIndex = circleRow.column.indexOf(circle)
            }
        }
        return if (rowIndex == -1 || columnIndex == -1) null else rowIndex to columnIndex
    }

    fun getCircleNeighbours(circle: Circle): List<Circle> {

        val circlePosition = getCirclePositionInField(circle)

        val neighbours = mutableListOf<Circle>()

        circlePosition?.let {
            //Левый
            if (circlePosition.first > 0) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first - 1,
                        circlePosition.second
                    )
                )
            }
            // Правый
            if (circlePosition.first < modifiableField.size - 1) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first + 1,
                        circlePosition.second
                    )
                )
            }

            // Верхний
            if (circlePosition.second > 0) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first,
                        circlePosition.second - 1
                    )
                )
            }
            // Нижний
            if (circlePosition.second < modifiableField[0].column.size - 1) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first,
                        circlePosition.second + 1
                    )
                )
            }

            // Левый верхний
            if (circlePosition.first > 0 && circlePosition.second > 0) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first - 1,
                        circlePosition.second - 1
                    )
                )
            }

            // Правый верхний
            if (circlePosition.first < modifiableField.size - 1 && circlePosition.second > 0) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first + 1,
                        circlePosition.second - 1
                    )
                )
            }

            // Левый нижний
            if (circlePosition.first > 0 && circlePosition.second < modifiableField[0].column.size - 1) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first - 1,
                        circlePosition.second + 1
                    )
                )
            }

            // Правый нижний
            if (circlePosition.first < modifiableField.size - 1 && circlePosition.second < modifiableField[0].column.size - 1) {
                neighbours.add(
                    getCircleByPosition(
                        circlePosition.first + 1,
                        circlePosition.second + 1
                    )
                )
            }


        }

        return neighbours
    }

    private fun addCircleToField(
        x: Double,
        y: Double,
        state: CircleState,
        rowIndex: Int,
        columnIndex: Int,
        field: MutableList<CircleColumn>
    ) {
        val oldColumn = field[rowIndex].column
        field[rowIndex] = field[rowIndex].copy(
            column = oldColumn.toMutableList().apply {
                this[columnIndex] =
                    Circle(
                        x,
                        y,
                        state
                    )
            }
        )
    }

    private fun getCircleByPosition(rowIndex: Int, columnIndex: Int): Circle {
        return modifiableField[rowIndex].column[columnIndex]
    }
}