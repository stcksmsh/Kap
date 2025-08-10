package io.github.stcksmsh.kap.wearable.ui.composables

import android.util.Log
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import kotlinx.coroutines.launch

@Composable
fun CircularCurvedButtonSelector(
    buttonLabels: List<String>,
    onButtonClick: (String) -> Unit
) {
    // State to track the index of the currently selected button
    var selectedIndex by remember { mutableIntStateOf(0) }

    // Infinite list of labels to create a scrolling illusion
    val infiniteLabels = remember { generateInfiniteList(buttonLabels) }

    // Box to center the CurvedLayout and add gesture detection
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 0) {
                        // Drag right: decrease index
                        selectedIndex = (selectedIndex - 1 + buttonLabels.size) % buttonLabels.size
                    } else if (dragAmount < 0) {
                        // Drag left: increase index
                        selectedIndex = (selectedIndex + 1) % buttonLabels.size
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Curved Layout for the arc effect
        CurvedLayout {
            infiniteLabels.forEachIndexed { index, label ->
                curvedComposable {
                    Button(
                        onClick = { onButtonClick(label) },
                        modifier = Modifier
                            .padding(8.dp),
                    ) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}


@Composable
fun CircularScrollableRow(
    buttonLabels: List<String>,
    onButtonClick: (Int) -> Unit
) {
    // Create an infinite scrolling list of buttons
    val infiniteLabels = remember { generateInfiniteList(buttonLabels) }
    val listSize = infiniteLabels.size
    val startIndex = listSize / 2 // Start in the middle of the list
    val listState = rememberLazyListState(startIndex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleX = 1.25f, scaleY = 1f),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            state = listState
        ) {
            itemsIndexed(infiniteLabels) { index, label ->
                Button(
                    onClick = {
                        onButtonClick(index % buttonLabels.size) },
                    modifier = Modifier
                        .padding(8.dp)
                        .scale(
                            scaleX = 0.8f, scaleY = 1f
                        ),
                ) {
                    Text(text = label)
                }
            }
        }
    }

    // Coroutine scope for programmatically scrolling
    val coroutineScope = rememberCoroutineScope()

    // Handle dynamic repositioning for infinite scrolling
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { currentIndex ->
                if (currentIndex <= 1) {
                    // Scroll to the middle when reaching the start
                    coroutineScope.launch {
                        listState.requestScrollToItem(listSize / 2 + currentIndex)
                    }
                } else if (currentIndex >= listSize - 2) {
                    // Scroll to the middle when reaching the end
                    coroutineScope.launch {
                        listState.requestScrollToItem(listSize / 2 - (listSize - currentIndex))
                    }
                }
            }
    }
}


fun <T> generateInfiniteList(inputList: List<T>): List<T> {
    val listSize = inputList.size * 4
    return List(listSize) { inputList[it % inputList.size] }
}
