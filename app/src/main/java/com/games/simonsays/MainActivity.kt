package com.games.simonsays

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.games.simonsays.ui.screen.SimonGameScreen
import com.games.simonsays.ui.theme.SimonSaysTheme
import com.games.simonsays.viewmodel.SimonGameViewModel

class MainActivity : ComponentActivity() {

    val simonGameViewModel: SimonGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimonSaysTheme {
                SimonGameScreen(simonGameViewModel = simonGameViewModel)
            }
        }
    }
}

