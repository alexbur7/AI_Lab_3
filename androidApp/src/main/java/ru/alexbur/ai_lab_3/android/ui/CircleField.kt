package ru.alexbur.ai_lab_3.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.alexbur.ai_lab_3.android.R
import ru.alexbur.ai_lab_3.circleRadius
import ru.alexbur.ai_lab_3.data.CircleColumn
import ru.alexbur.ai_lab_3.data.CircleOffset
import ru.alexbur.ai_lab_3.data.CircleState
import ru.alexbur.ai_lab_3.data.calculateCircleCount
import ru.alexbur.ai_lab_3.data.processor.CircleFieldProcessor

@Composable
fun CircleFieldComposable(
    modifier: Modifier
) {
    val fieldProcessor = remember { CircleFieldProcessor() }
    val modifiableField = remember { mutableStateListOf<CircleColumn>() }
    val initialField = remember { mutableStateListOf<CircleColumn>() }

    var totalCirclesX by remember {
        mutableIntStateOf(0)
    }

    var totalCirclesY by remember {
        mutableIntStateOf(0)
    }

    var isEditableField by remember { mutableStateOf(true) }

    LaunchedEffect(totalCirclesX, totalCirclesY) {
        val data = fieldProcessor.init(totalCirclesX, totalCirclesY)
        replaceCircle(initialField, data)
        replaceCircle(modifiableField, data)
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Canvas(modifier = modifier
            .fillMaxHeight(0.75f)
            .pointerInput(true) {
                detectTapGestures {
                    if (isEditableField) {
                        replaceCircle(
                            modifiableField,
                            fieldProcessor.moveTappedCircle(CircleOffset(it.x.toDouble(), it.y.toDouble()))
                        )
                    }
                }
            }) {

            val data = calculateCircleCount(size.width, size.height)
            totalCirclesX = data.first
            totalCirclesY = data.second
            drawCircles(if (isEditableField) modifiableField else initialField)
        }

        Button(
            onClick = {
                replaceCircle(modifiableField, fieldProcessor.moveUnhappyCircles())
            },
            enabled = isEditableField
        ) {
            Text(text = stringResource(id = R.string.render))
        }

        Spacer(modifier = Modifier.height(3.dp))

        Button(
            onClick = {
                isEditableField = false
                val data = fieldProcessor.init(totalCirclesX, totalCirclesY)
                replaceCircle(initialField, data)
                replaceCircle(modifiableField, data)
            },
        ) {
            Text(text = stringResource(id = R.string.create_new_field))
        }

        Spacer(modifier = Modifier.height(3.dp))

        Switch(
            checked = isEditableField,
            onCheckedChange = {
                isEditableField = it
            }
        )
    }
}

private fun replaceCircle(
    modifiableField: SnapshotStateList<CircleColumn>,
    data: List<CircleColumn>
) {
    modifiableField.clear()
    modifiableField.addAll(data)
}

private fun DrawScope.drawCircles(field: SnapshotStateList<CircleColumn>) {
    field.forEach { circleColumn ->
        circleColumn.column.forEach {
            val color = when (it.state) {
                CircleState.RED -> Color.Red
                CircleState.BLUE -> Color.Blue
                CircleState.EMPTY -> Color.Gray
            }
            drawCircle(
                color,
                circleRadius,
                Offset(it.centerX.toFloat(), it.centerY.toFloat())
            )
        }
    }
}