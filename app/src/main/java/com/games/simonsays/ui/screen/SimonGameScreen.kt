package com.games.simonsays.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.games.simonsays.R
import com.games.simonsays.model.SimonButtonModel
import com.games.simonsays.ui.theme.Shapes
import com.games.simonsays.utils.GameStatus
import com.games.simonsays.utils.SimonColor
import com.games.simonsays.viewmodel.SimonGameViewModel


@Composable
fun SimonGameScreen(simonGameViewModel: SimonGameViewModel) {
    SimonGame(
        simonGameViewModel.simonGame,
        onSimonButtonClick = simonGameViewModel::onSimonButtonClick,
        onPlayButtonClick = simonGameViewModel::onPlayButtonClick,
        gameStatus = simonGameViewModel.gameStatus,
        turn = simonGameViewModel.turn,
        headerVisibility = simonGameViewModel.headerVisibility
    )
}

@Composable
fun SimonGame(
    simonGame: Map<SimonColor,SimonButtonModel>,
    onSimonButtonClick: (SimonButtonModel) -> Unit,
    onPlayButtonClick: () -> Unit,
    gameStatus: GameStatus,
    headerVisibility: Boolean,
    turn: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Header(
            modifier = Modifier.align(Alignment.TopCenter),
            headerVisibility = headerVisibility,
            turn = turn
        )
        SimonButtons(
            simonGame = simonGame,
            onSimonButtonClick = onSimonButtonClick,
            gameStatus = gameStatus
        )
        PlayButton (
            onPlayButtonClick = onPlayButtonClick,
            gameStatus = gameStatus
        )

    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    headerVisibility: Boolean,
    turn: Int
) {
    val shadowOffset: Offset by animateOffsetAsState(
        targetValue =
            if (headerVisibility)
                Offset(5.0f, 10.0f)
            else
                Offset(0f, 0f)
    )
    val color: Color by animateColorAsState(
        targetValue =
            if(headerVisibility)
                Color.DarkGray
            else
                Color.Gray
    )
    Text(
        modifier = modifier
            .padding(8.dp),
        text = "Level $turn",
        style = MaterialTheme.typography.h4.copy(
            shadow = Shadow(
                color = color,
                offset = shadowOffset,
                blurRadius = 5f
            )
        ),
        color = Color.Gray
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayButton(
    gameStatus: GameStatus,
    onPlayButtonClick: () -> Unit
) {
    val elevation: Dp by animateDpAsState(if (gameStatus == GameStatus.WaitForBegin) 10.dp else 0.dp)
    IconButton(
        enabled = gameStatus == GameStatus.WaitForBegin,
        onClick = onPlayButtonClick
    ) {
        Surface(
            modifier = Modifier.size(75.dp),
            shape = RoundedCornerShape(100),
            elevation = elevation,
            color = Color.Gray
        ) {
            AnimatedVisibility(
                visible = gameStatus == GameStatus.WaitForBegin,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(16.dp),
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "playButton",
                    tint = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun SimonButtons(
    simonGame: Map<SimonColor,SimonButtonModel>,
    onSimonButtonClick: (SimonButtonModel) -> Unit,
    gameStatus: GameStatus
) {
    Column(modifier = Modifier.padding(4.dp)) {
        Row {
            SimonButton(
                rotateDegrees = 90F,
                simonButton = simonGame[SimonColor.Green]!!,
                onClick = onSimonButtonClick,
                gameStatus = gameStatus
            )
            SimonButton(
                rotateDegrees = 180F,
                simonButton = simonGame[SimonColor.Red]!!,
                onClick = onSimonButtonClick,
                gameStatus = gameStatus
            )
        }
        Row {
            SimonButton(
                rotateDegrees = 0F,
                simonButton = simonGame[SimonColor.Yellow]!!,
                onClick = onSimonButtonClick,
                gameStatus = gameStatus
            )
            SimonButton(
                rotateDegrees = 270F,
                simonButton = simonGame[SimonColor.Blue]!!,
                onClick = onSimonButtonClick,
                gameStatus = gameStatus
            )
        }
    }
}

@Composable
fun SimonButton(
    rotateDegrees: Float,
    simonButton: SimonButtonModel,
    onClick: (SimonButtonModel) -> Unit,
    gameStatus: GameStatus,
) {
    Image(
        modifier = Modifier
            .rotate(rotateDegrees)
            .size(100.dp)
            .padding(2.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = !simonButton.isLight && gameStatus == GameStatus.PlayerTurn,
                onClick = { onClick(simonButton) }
            ),
        painter = painterResource(id = R.drawable.ic_simon_button_image),
        contentDescription = "simon button",
        colorFilter = ColorFilter.tint(getColorBySimonColor(simonButton.color)),
        alpha = if(simonButton.isLight) 1f else 0.3f
    )
}

private fun getColorBySimonColor(simonColor: SimonColor): Color = when(simonColor) {
    SimonColor.Blue -> Color.Blue
    SimonColor.Yellow -> Color.Yellow
    SimonColor.Red -> Color.Red
    SimonColor.Green -> Color.Green
}
