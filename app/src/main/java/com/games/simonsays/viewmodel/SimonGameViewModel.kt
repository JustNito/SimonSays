package com.games.simonsays.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.games.simonsays.model.SimonButtonModel
import com.games.simonsays.utils.GameStatus
import com.games.simonsays.utils.SimonColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimonGameViewModel: ViewModel() {

    private var _gameStatus by mutableStateOf(GameStatus.WaitForBegin)
    val gameStatus
        get() = _gameStatus

    private var _headerVisibility by mutableStateOf(true)
    val headerVisibility
        get() = _headerVisibility

    val simonGame = mutableStateMapOf(
        SimonColor.Blue to SimonButtonModel(color = SimonColor.Blue),
        SimonColor.Red to SimonButtonModel(color = SimonColor.Red),
        SimonColor.Yellow to SimonButtonModel(color = SimonColor.Yellow),
        SimonColor.Green to SimonButtonModel(color = SimonColor.Green)
    )

    private val lightsOrder = mutableListOf<SimonColor>()

    private var _turn = 1
    val turn
        get() = _turn

    private fun populateLightsOrder() {
        lightsOrder.clear()
        repeat(_turn) {
            lightsOrder.add(SimonColor.values().random())
        }
    }

    fun onPlayButtonClick() {
        populateLightsOrder()
        playRecord()
    }

    private fun playRecord() = viewModelScope.launch {
        _gameStatus = GameStatus.Animation
        lightsOrder.forEach {
            delay(1000)
            changeLighting(it,true)
            delay(1000)
            changeLighting(it,false)
        }
        _gameStatus = GameStatus.PlayerTurn
    }

    fun onSimonButtonClick(simonButtonModel: SimonButtonModel) = viewModelScope.launch {
        _gameStatus = GameStatus.Animation
        changeLighting(simonButtonModel.color, true)
        delay(1000)
        changeLighting(simonButtonModel.color, false)
        if(lightsOrder.removeFirst() != simonButtonModel.color) {
            _gameStatus = GameStatus.GameOver
            loss()
        } else {
            _gameStatus = GameStatus.PlayerTurn
        }
        if(lightsOrder.isEmpty()) {
            launch { headerChange(_turn + 1) }
            win()
            _gameStatus = GameStatus.WaitForBegin
        }
    }

    private suspend fun headerChange(turn: Int) {
        _headerVisibility = false
        delay(1000)
        _turn = turn
        _headerVisibility = true
    }

    private suspend fun win() {
        val simonColorInRightOrder = listOf(
            SimonColor.Green,
            SimonColor.Red,
            SimonColor.Blue,
            SimonColor.Yellow
        )
        repeat(3) {
            simonColorInRightOrder.forEach {
                changeLighting(it, true)
                delay(100)
                changeLighting(it, false)
            }
        }
        _gameStatus = GameStatus.WaitForBegin
    }

    private suspend fun loss() {
        repeat(3) {
            delay(500)
            simonGame.forEach { changeLighting(it.key, true) }
            delay(500)
            simonGame.forEach { changeLighting(it.key, false) }
        }
        headerChange(1)
        _gameStatus = GameStatus.WaitForBegin
    }

    private fun changeLighting(simonColor: SimonColor, isLight: Boolean) {
        simonGame[simonColor] = simonGame[simonColor]!!.copy(isLight = isLight)
    }
}