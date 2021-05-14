package com.sauravrp.tictactoe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicTacToeViewModel : ViewModel() {

    private var firstUser = true
    private var gameOver = false

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> by lazy {
        _viewState
    }

    private val _viewEvent = MutableLiveData<ViewEvent>()
    val viewEvent: LiveData<ViewEvent> by lazy {
        _viewEvent
    }

    lateinit var board: Board

    var boardSize: Int = 0

    fun init() {
        _viewState.value = ViewState.Init
    }

    fun setupBoard(size: Int) {
        firstUser = true
        gameOver = false
        boardSize = size
        var pos = 0
        board = mutableListOf<Row>()
        for (i in 0 until size) {
            val rowList = mutableListOf<Piece>()
            for (j in 0 until size) {
                rowList.add(Piece(pos, Type.EMPTY))
                pos++
            }
            board.add(rowList)
        }

        updateViewBoard(board)

        _viewEvent.value = ViewEvent.UserTurn(firstUser)
    }

    fun pieceSelected(piece: Piece) {
        if (gameOver)
            return

        replace(board, piece.position, if (firstUser) Type.X else Type.O)

        // do some processing
        updateViewBoard(board)

        if (isDraw(board)) {
            gameOver = true
            _viewEvent.value = ViewEvent.Draw
            return
        } else if (didUserWin(board, piece)) {
            gameOver = true
            _viewEvent.value = ViewEvent.UserWon(firstUser)
            return
        }

        firstUser = !firstUser

        _viewEvent.value = ViewEvent.UserTurn(firstUser)
    }

    private fun isDraw(board: Board): Boolean {
        // if none of the rows are emtpy
        return (board.map { it.all { it.type != Type.EMPTY } }.toList().none { !it })
    }

    private fun didUserWin(board: Board, piece: Piece): Boolean {
        fun getRow(board: Board, row: Int): Row {
            return board[row]
        }

        fun getCol(board: Board, col: Int): Column {
            return board.map { row ->
                row[col]
            }.toList()
        }

        fun getDiagonal(board: Board): Diagonal {
            return board.mapIndexed { rowIndex, row ->
                row[rowIndex]
            }.toList()
        }

        /**
         *
         *  0 1 2    (0,2)
         *  3 4 5  (1,1)
         *  6 7 8 (2, 0)
         */
        fun getReverseDiagonal(board: Board): Diagonal {
            val diagList = mutableListOf<Piece>()

            val colSize = board.size - 1
            var colIndex = colSize
            var rowIndex = 0

            for (i in 0..colSize) {
                diagList.add(board[rowIndex][colIndex])
                rowIndex++
                colIndex--
            }

            return diagList
        }

        /**
         * 0 - row 0 = 0/3 = 0
         * 1 - row 0 = 1/3 = 0
         * 2 - row 0
         * 3 - row 1 = 1/3 = 1
         * 4 - row 1 4/3 = 1
         */
        fun rowArrayPosition(piecePosition: Int, boardSize: Int): Int {
            return piecePosition / boardSize
        }

        /**
         * 0 - col 0 0 % 3 = 0
         * 1 - col 1  1 % 3 = 1
         * 2 - col 2
         * 3 - col 0
         * 4 - col 1 4 % 3 = 1
         */

        fun colArrayPosition(piecePosition: Int, boardSize: Int): Int {
            return piecePosition % boardSize
        }

        val diagonalWinner = getDiagonal(board).all { it.type == Type.X } ||
                getDiagonal(board).all { it.type == Type.O }


        val reverseDiagonalWinner = getReverseDiagonal(board).all { it.type == Type.X } ||
                getReverseDiagonal(board).all { it.type == Type.O }

        val rowWinner = (getRow(board, rowArrayPosition(piece.position, board.size)).all { it.type == Type.X } ||
                getRow(board, rowArrayPosition(piece.position, board.size)).all { it.type == Type.O })

        val columnWinner = (getCol(board, colArrayPosition(piece.position, board.size)).all { it.type == Type.X } ||
                getCol(board, colArrayPosition(piece.position, board.size)).all { it.type == Type.O })

        return reverseDiagonalWinner || diagonalWinner || rowWinner || columnWinner
    }

    private fun replace(board: Board, position: Int, type: Type) {
        board.map { row ->
            row.mapIndexed { index, piece ->
                if (piece.position == position) {
                    row[index] = piece.copy(position = piece.position, type = type)
                }
            }
        }
    }

    private fun updateViewBoard(board: Board) {
        _viewState.value = board.toViewState()
    }
}

// internal
typealias Diagonal = List<Piece>
typealias Column = List<Piece>
typealias Row = MutableList<Piece>
typealias Board = MutableList<Row>


// consumed by view
sealed class ViewEvent {
    data class UserTurn(val firstUser: Boolean) : ViewEvent()
    data class UserWon(val firstUser: Boolean) : ViewEvent()
    object Draw : ViewEvent()
}

sealed class ViewState {
    object Init : ViewState()
    data class Success(val boardSize: Int, val pieces: List<Piece>) : ViewState()
}

data class Piece(val position: Int, val type: Type)

fun Piece.copy(type: Type): Piece {
    return Piece(position, type)
}

enum class Type {
    EMPTY, O, X
}

fun Board.toViewState(): ViewState.Success {
    val listPieces = mutableListOf<Piece>()
    map { row: Row ->
        listPieces.addAll(row)
    }
    return ViewState.Success(size, listPieces)
}


