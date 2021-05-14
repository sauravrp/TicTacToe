package com.sauravrp.tictactoe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicTacToeViewModel : ViewModel() {

    var firstUser = true

    private val _viewState = MutableLiveData<ViewState>()
    val viewState : LiveData<ViewState> by lazy {
        _viewState
    }

    private val _viewEvent = MutableLiveData<ViewEvent>()
    val viewEvent : LiveData<ViewEvent> by lazy {
        _viewEvent
    }

    lateinit var board : Board

    var boardSize: Int = 0

    fun init(size: Int) {
        boardSize = size
        var pos = 0
        board = mutableListOf<Row>()
        for(i in 0 until size) {
            val rowList = mutableListOf<Piece>()
            for(j in 0 until size) {
                rowList.add(Piece(pos, Type.EMPTY))
                pos++
            }
            board.add(rowList)
        }

        updateViewBoard()
    }

    fun pieceSelected(piece: Piece) {
        firstUser = !firstUser

        replace(piece.position, Type.O)

        // do some processing
        updateViewBoard()
    }

    private fun replace(position: Int, type: Type) {
        board.map { row ->
            row.mapIndexed { index, piece ->
                if(piece.position == position) {
                    row[index] = piece.copy(position = piece.position, type = type)
                }
            }
        }
    }

    private fun updateViewBoard() {
        _viewState.value = board.toViewState()
    }
}

// internal
typealias Row = MutableList<Piece>
typealias Board = MutableList<Row>


// consumed by view
sealed class ViewEvent {
    object FirstUserWon : ViewEvent()
    object SecondUserWon : ViewEvent()
    object Draw : ViewEvent()
}


data class ViewState(val pieces: List<Piece>)

data class Piece(val position: Int, val type: Type)

fun Piece.copy(type: Type) : Piece {
    return Piece(position, type)
}

enum class Type {
    EMPTY, O, X
}

fun Board.toViewState() : ViewState {
    val listPieces = mutableListOf<Piece>()
    val board = ViewState(listPieces)
    map { row: Row ->
        listPieces.addAll(row)
    }
    return board
}


