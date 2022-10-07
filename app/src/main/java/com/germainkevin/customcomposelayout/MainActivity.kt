package com.germainkevin.customcomposelayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.customcomposelayout.ui.theme.CustomComposeLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomComposeLayoutTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 0.dp)
                        .background(MaterialTheme.colorScheme.background),
                ) {
//                    DisplayBasicLayouts()
                    SimpleTopBar(
                        centeredTitleWhenCollapsed = false,
                        title = {
                            Text(
                                text = "SmartWork", style = LocalTextStyle.current.copy(
                                    fontSize = 24.sp
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = Icons.Default.Menu.name
                                )
                            }
                        },
                        mainAction = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = Icons.Default.FavoriteBorder.name
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = Icons.Default.MoreVert.name
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayBasicLayouts() {
    Spacer(modifier = Modifier.height(8.dp))
    BasicColumn(
        modifier = Modifier
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
    ) {
        Text("BasicColumn", color = MaterialTheme.colorScheme.onPrimary)
        Text("places items", color = MaterialTheme.colorScheme.onPrimary)
        Text("vertically.", color = MaterialTheme.colorScheme.onPrimary)
        Text("We've done it by hand!", color = MaterialTheme.colorScheme.onPrimary)
    }
    Spacer(modifier = Modifier.height(8.dp))
    BasicRow(
        modifier = Modifier
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
    ) {
        Text("BasicRow", color = MaterialTheme.colorScheme.onPrimary)
        Text(" places items", color = MaterialTheme.colorScheme.onPrimary)
        Text(" horizontally.", color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun BasicRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable -> measurable.measure(constraints) }

        val sumOfWidths = placeables.sumOf { it.width }

        val maxLength =
            if (sumOfWidths > constraints.maxWidth) constraints.maxWidth else sumOfWidths
        val longestPlaceable = placeables.maxOf { it.height }

        layout(maxLength, longestPlaceable) {
            var xPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = xPosition, y = 0)
                xPosition += placeable.width
            }
        }
    }
}


@Composable
fun BasicColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable -> measurable.measure(constraints) }

        val largestPlaceable = placeables.maxOf { it.width }
        val longestPlaceable = placeables.sumOf { it.height }

        layout(largestPlaceable, longestPlaceable) {
            var yPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}
