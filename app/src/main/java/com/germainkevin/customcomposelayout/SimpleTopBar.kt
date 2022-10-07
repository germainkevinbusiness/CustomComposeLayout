package com.germainkevin.customcomposelayout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber
import kotlin.math.max
import kotlin.math.roundToInt


private val TopBarHorizontalPadding = 4.dp

// A title inset when the App-Bar is a Medium or Large one. Also used to size a spacer when the
// navigation icon is missing.
private val TopBarTitleInset = 16.dp - TopBarHorizontalPadding

@Composable
fun SimpleTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    mainAction: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    currentTopBarHeight: Dp = 56.dp,
    centeredTitleWhenCollapsed: Boolean = false,
) {
    SingleRowTopBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        mainAction = mainAction,
        currentTopBarHeight = currentTopBarHeight,
        actions = actions,
        centeredTitleWhenCollapsed = centeredTitleWhenCollapsed
    )
}

@Composable
fun SingleRowTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    currentTopBarHeight: Dp,
    centeredTitleWhenCollapsed: Boolean,
) {

    // Wrap the given actions in a Row.
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }

    Surface(modifier = modifier, color = MaterialTheme.colorScheme.primary) {
        val height = LocalDensity.current.run {
            currentTopBarHeight.toPx()
        }
        TopBarLayout(
            heightPx = height,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            title = title,
            titleTextStyle = LocalTextStyle.current,
            centeredTitleWhenCollapsed = centeredTitleWhenCollapsed,
            navigationIcon = navigationIcon,
            mainAction = mainAction,
            actions = actionsRow,
        )
    }
}

@Composable
fun TopBarLayout(
    heightPx: Float,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    modifier: Modifier = Modifier,
    titleAlpha: Float = 1f,
    navigationIcon: @Composable () -> Unit = {},
    mainAction: @Composable () -> Unit,
    actions: @Composable () -> Unit = {},
    centeredTitleWhenCollapsed: Boolean,
) {
    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon
                )
            }
            Box(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = TopBarHorizontalPadding)
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        LocalContentColor provides titleContentColor.copy(alpha = titleAlpha),
                        content = title
                    )
                }
            }
            Box(
                Modifier
                    .layoutId("mainAction")
                    .padding(start = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = mainAction
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions
                )
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables.first { it.layoutId == "navigationIcon" }.measure(constraints)
        val mainActionIconPlaceable =
            measurables.first { it.layoutId == "mainAction" }.measure(constraints)
        val actionIconsPlaceable =
            measurables.first { it.layoutId == "actionIcons" }.measure(constraints)

        val maxTitleWidth =
            constraints.maxWidth - navigationIconPlaceable.width -
                    mainActionIconPlaceable.width - actionIconsPlaceable.width

        val titlePlaceable =
            measurables
                .first { it.layoutId == "title" }
                .measure(constraints.copy(maxWidth = maxTitleWidth))

        val layoutHeight = heightPx.roundToInt()

        layout(constraints.maxWidth, layoutHeight) {

            // Locate the title's baseline.
            val titleBaseline =
                if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
                    titlePlaceable[LastBaseline]
                } else {
                    0
                }

            // Have no clue why this positioning is working, but it works !
            val navIconYPosInCenter = titleBaseline / 3

            // Navigation icon
            navigationIconPlaceable.placeRelative(x = 0, y = navIconYPosInCenter)

            val mainActionWidth = mainActionIconPlaceable.width.toFloat()
            val actionsWidth = actionIconsPlaceable.width.toFloat()

            val horizPaddingPx = TopBarHorizontalPadding.toPx()

            val placeTitleNextToNavIcon =
                max(TopBarTitleInset.roundToPx(), navigationIconPlaceable.width)
            val placeTitleInCenter =
                if (mainActionWidth > horizPaddingPx && actionsWidth > horizPaddingPx) {
                    (constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width) / 2
                } else {
                    (constraints.maxWidth - titlePlaceable.width) / 2
                }

            // Title
            titlePlaceable.placeRelative(
                x = if (centeredTitleWhenCollapsed) placeTitleInCenter else placeTitleNextToNavIcon,
                y = (layoutHeight - titlePlaceable.height) / 2
            )

            // Main Action Icon
            mainActionIconPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width - mainActionIconPlaceable.width,
                y = (layoutHeight - mainActionIconPlaceable.height) / 2
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2
            )

            Timber.d("titleBaseline Calculus:$titleBaseline")
            Timber.d("navIconYPosInCenter Calculus:$navIconYPosInCenter")
            Timber.d("title y pos Calculus:${(layoutHeight - titlePlaceable.height) / 2}")
        }
    }
}